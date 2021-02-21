package bg.sofia.uni.fmi.mjt.analyzer.server;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.command.Command;
import bg.sofia.uni.fmi.mjt.analyzer.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStartException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStopException;
import bg.sofia.uni.fmi.mjt.analyzer.handler.CommandExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@code FoodAnalyzerServer} is a server which can handle multiple client connections
 * and client requests at the same time and is used by the clients to search for an
 * information about foods
 */

public class FoodAnalyzerServer implements Server {
    private static final Logger LOGGER = Logger.getLogger(FoodAnalyzerServer.class.getName());
    private static final String SERVER_STARTED_MESSAGE = "Server started";
    private static final String SERVER_CLOSED_MESSAGE = "Server closed";
    private static final String SERVER_CLOSING_MESSAGE_TO_CLIENTS = "The server is closed, please try again later";
    private static final String SERVER_START_FAIL_MESSAGE = "Failed to start the server";
    private static final String SERVER_STOP_FAIL_MESSAGE = "Failed to stop the server";
    private static final String CLIENT_REQUEST_ERROR_MESSAGE = "Error occurred while processing client request";
    private static final String WHITE_SPACE_REGEX = "\\s+";
    private static final String INVALID_COMMAND_MESSAGE = """
            There is no such command : '%s' \
            Supported commands are: 'get-food [food name]', 'get-food-report [food id]', \
            'get-food-by-barcode [--img=UPC_IMAGE_FOLDER_PATH or --code=FOOD_UPC_CODE]'\
            """;
    private static final String CLIENT_CONNECTED_MESSAGE = "A client connected: %s";
    private static final String CLIENT_DISCONNECTED_MESSAGE = "A client disconnected: %s";
    private static final String FAILED_TO_SEND_SERVER_CLOSING_MESSAGE = "Client lost connection with the server before being notified about the closing";
    private static final int BUFFER_SIZE = 512;
    private static final int REQUEST_HANDLING_THREADS = 20;
    private static final int COMMAND_NAME_INDEX = 0;
    private static final int COMMAND_NAME_ARGUMENT = 1;

    private Selector selector;
    private ByteBuffer buffer;
    private boolean isRunning;
    private int port;
    private String host;
    private FoodService foodApi;
    private ExecutorService requestHandlerExecutor;
    private CommandFactory commandFactory;

    /**
     * Constructs a {@code FoodAnalyzerServer} which will run on the specified port and host,
     * will retrieve information about foods from a {@code FoodHttpApi}
     *
     * @param port    The port on which the server will be running
     * @param host    The host on which the server will be running
     * @param foodApi The third party API from which the server will retrieve information about foods
     */

    public FoodAnalyzerServer(int port, String host, FoodService foodApi) {
        this.port = port;
        this.host = host;
        this.foodApi = foodApi;
    }

    @Override
    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            initializeFields();
            LOGGER.log(Level.INFO, SERVER_STARTED_MESSAGE);

            while (isRunning) {
                try {
                    manageSelectionKeys();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, CLIENT_REQUEST_ERROR_MESSAGE, e);
                }
            }
        } catch (IOException e) {
            throw new FailedToStartException(SERVER_START_FAIL_MESSAGE, e);
        }
    }

    @Override
    public void stop() {
        isRunning = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }

        notifyClientsAboutClosing();
        requestHandlerExecutor.shutdown();
        try {
            selector.close();
        } catch (IOException e) {
            throw new FailedToStopException(SERVER_STOP_FAIL_MESSAGE, e);
        }
        LOGGER.log(Level.INFO, SERVER_CLOSED_MESSAGE);
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(host, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void initializeFields() {
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        requestHandlerExecutor = Executors.newFixedThreadPool(REQUEST_HANDLING_THREADS);
        commandFactory = new CommandFactory(foodApi);
        isRunning = true;
    }

    private void manageSelectionKeys() throws IOException {
        int readyChannels = selector.select();
        if (readyChannels == 0) {
            return;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                read(key);
            } else if (key.isAcceptable()) {
                accept(key);
            }
            keyIterator.remove();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            String clientInput = getClientInput(clientChannel);
            if (clientInput == null) {
                return;
            }

            executeClientRequest(clientChannel, clientInput);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, CLIENT_DISCONNECTED_MESSAGE.formatted(clientChannel.getLocalAddress()), e);
            /* Lost connection with client socket, so close the channel */
            clientChannel.close();
        }
    }

    private void executeClientRequest(SocketChannel clientChannel, String clientInput) throws IOException {
        String[] inputArguments = clientInput.split(WHITE_SPACE_REGEX);
        String commandName = inputArguments[COMMAND_NAME_INDEX];
        Command command = commandFactory.getCommand(commandName);
        if (command == null) {
            writeOutputToClient(clientChannel, INVALID_COMMAND_MESSAGE.formatted(commandName));
            return;
        }

        String[] commandArguments = Arrays.stream(inputArguments).skip(COMMAND_NAME_ARGUMENT).toArray(String[]::new);
        requestHandlerExecutor.execute(new CommandExecutor(clientChannel, command, commandArguments));
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        int r = clientChannel.read(buffer);
        if (r < 0) {
            /* Client closed connection, disconnect him from the server */
            LOGGER.log(Level.INFO, CLIENT_DISCONNECTED_MESSAGE.formatted(clientChannel.getLocalAddress()));
            clientChannel.close();
            return null;
        }

        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private void writeOutputToClient(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
        LOGGER.log(Level.INFO, CLIENT_CONNECTED_MESSAGE.formatted(accept.getLocalAddress()));
    }

    private void notifyClientsAboutClosing() {
        selector.keys().forEach(k -> {
            SocketChannel clientChannel;
            if (k.channel() instanceof SocketChannel) {
                clientChannel = (SocketChannel) k.channel();
                try {
                    writeOutputToClient(clientChannel, SERVER_CLOSING_MESSAGE_TO_CLIENTS);
                } catch (IOException e) {
                    LOGGER.log(Level.INFO, FAILED_TO_SEND_SERVER_CLOSING_MESSAGE, e);
                }
            }
        });
    }
}
