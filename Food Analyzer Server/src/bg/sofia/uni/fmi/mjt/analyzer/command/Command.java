package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;

/**
 * An object which is used to execute commands based on provided arguments
 */

public interface Command {

    /**
     * @param arguments The arguments which will be used to execute the command
     * @return The result of the command execution
     * @throws FoodServiceException            If an error occurs while trying to retrieve data from the food service
     * @throws InvalidCommandArgumentException If a provided argument is not valid
     */

    String execute(String... arguments) throws FoodServiceException, InvalidCommandArgumentException;

}
