package bg.sofia.uni.fmi.mjt.analyzer.handler;

import bg.sofia.uni.fmi.mjt.analyzer.command.Command;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A runnable object which is used to execute a command received by the client and return a proper response to him
 */

public class CommandExecutor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getName());
    private static final String CLIENT_LOST_CONNECTION_MESSAGE = "A client lost connection with the server";
    private static final String COMMAND_FAILED_MESSAGE = "Failed to execute command: %s";
    private static final int BUFFER_SIZE = 4096;
    private final SocketChannel clientChannel;
    private Command command;
    private String[] commandArguments;
    private ByteBuffer outputBuffer;

    /**
     * Constructs a new {@code CommandExecutor} which accepts a command with arguments to execute
     * and the client which requested the command
     *
     * @param clientChannel    The channel which requested the command and which will receive the result of the executed command
     * @param command          The command requested by the client
     * @param commandArguments The provided arguments by the client which the command will execute
     */

    public CommandExecutor(SocketChannel clientChannel, Command command, String... commandArguments) {
        this.clientChannel = clientChannel;
        this.command = command;
        this.commandArguments = commandArguments;
        outputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    @Override
    public void run() {
        try {
            String response = command.execute(commandArguments);
            LOGGER.log(Level.FINEST, response);
            writeOutputToClient(response);
        } catch (FoodServiceException | InvalidCommandArgumentException e) {
            LOGGER.log(Level.INFO, COMMAND_FAILED_MESSAGE.formatted(e.getMessage()));
            writeOutputToClient(e.getMessage());
        }
    }

    private void writeOutputToClient(String output) {
        outputBuffer.clear();
        outputBuffer.put(output.getBytes());
        outputBuffer.flip();
        synchronized (clientChannel) {
            try {
                clientChannel.write(outputBuffer);
            } catch (IOException e) {
                /* client lost connection */
                LOGGER.log(Level.INFO, CLIENT_LOST_CONNECTION_MESSAGE, e);
            }
        }
    }
}
