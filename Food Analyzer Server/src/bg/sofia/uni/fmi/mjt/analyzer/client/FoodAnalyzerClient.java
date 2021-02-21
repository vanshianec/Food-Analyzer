package bg.sofia.uni.fmi.mjt.analyzer.client;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStartException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStopException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * An object used to communicate with a {@code FoodAnalyzerServer} object by sending request
 * messages and receiving responses from the server
 */

public class FoodAnalyzerClient implements Client {
    private static final String NOT_CONNECTED_MESSAGE = "You are not connected to the server. Please try to reconnect";
    private static final String CLIENT_INITIALIZATION_ERROR = "There is a problem initializing the client";
    private static final String CONNECTION_LOST_ERROR = "Lost connection with server";
    private static final String ERROR_CLOSING_CLIENT = "An error occurred while trying to close the client";
    private static final int BUFFER_DEFAULT_SIZE = 4096;

    private SocketChannel socketChannel;
    private ByteBuffer buffer;
    private String serverHost;
    private int serverPort;
    private boolean isConnected;

    /**
     * Creates an instance of {@code WishListClient} which will connect to a server with
     * host {@code serverHost} and port {@code serverPort}
     *
     * @param serverPort The server port which the client will connect to
     * @param serverHost The server host which the client will connect to
     */

    public FoodAnalyzerClient(int serverPort, String serverHost) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void start() {
        isConnected = true;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        } catch (IOException e) {
            stop();
            throw new FailedToStartException(CLIENT_INITIALIZATION_ERROR, e);
        }
        buffer = ByteBuffer.allocateDirect(BUFFER_DEFAULT_SIZE);
    }

    @Override
    public void stop() {
        isConnected = false;
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new FailedToStopException(ERROR_CLOSING_CLIENT, e);
        }
    }

    @Override
    public String sendMessage(String message) {
        if (!isConnected) {
            return NOT_CONNECTED_MESSAGE;
        }

        readMessage(message);
        String response;
        try {
            response = getResponse();
        } catch (IOException e) {
            stop();
            return CONNECTION_LOST_ERROR;
        }

        return response;
    }

    private String getResponse() throws IOException {
        String response;
        socketChannel.write(buffer);
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        response = new String(byteArray, StandardCharsets.UTF_8);
        return response;
    }

    private void readMessage(String message) {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
    }
}
