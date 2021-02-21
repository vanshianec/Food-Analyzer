package bg.sofia.uni.fmi.mjt.analyzer.client;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStartException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStopException;

/**
 * An object used to communicate with a {@code Server} object
 */

public interface Client {

    /**
     * Establishes a connection with the server.
     *
     * @throws FailedToStartException If the client fails to establish a connection with the server
     */

    void start();

    /**
     * Closes the connection with the server.
     *
     * @throws FailedToStopException If the client fails to stop
     */

    void stop();

    /**
     * Sends a message command to the server
     *
     * @param message The message command which will be sent to the server
     * @return The response from the server
     */

    String sendMessage(String message);
}
