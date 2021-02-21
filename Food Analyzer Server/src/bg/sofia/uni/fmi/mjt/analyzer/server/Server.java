package bg.sofia.uni.fmi.mjt.analyzer.server;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStartException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FailedToStopException;

import java.io.IOException;

/**
 * A {@code Server} is used to establish a connection with one or more clients
 * which can send requests and receive responses from the server
 */

public interface Server {

    /**
     * Starts the server
     *
     * @throws FailedToStartException If an error occurs while trying to start the server
     */

    void start();

    /**
     * Stops the server
     *
     * @throws FailedToStopException If an error occurs while trying to stop the server
     */

    void stop() throws FailedToStopException;
}
