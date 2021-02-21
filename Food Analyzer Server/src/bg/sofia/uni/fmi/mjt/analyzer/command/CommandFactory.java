package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.storage.cache.FoodBarcodesCache;
import bg.sofia.uni.fmi.mjt.analyzer.storage.cache.FoodReportsCache;
import bg.sofia.uni.fmi.mjt.analyzer.storage.cache.FoodsCache;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory object used to retrieve command objects
 */

public final class CommandFactory {

    private static final String GET_FOOD_COMMAND = "get-food";
    private static final String GET_FOOD_REPORT_COMMAND = "get-food-report";
    private static final String GET_FOOD_BY_BARCODE_COMMAND = "get-food-by-barcode";

    private Map<String, Command> commands;

    /**
     * Constructs a new {@code CommandFactory} with a given food service
     *
     * @param foodService The service which will be used to create the {@code Command} objects
     */

    public CommandFactory(FoodService foodService) {
        this.commands = new HashMap<>();
        initializeCommands(foodService);
    }

    /**
     * Retrieves a command object by its name
     *
     * @param commandName The name of the command to be retrieved
     * @return The command object which matches the given name or null if there is no such command
     */

    public Command getCommand(String commandName) {
        if (commandName == null) {
            return null;
        }

        commandName = commandName.strip().toLowerCase();
        return commands.get(commandName);
    }

    private void initializeCommands(FoodService foodApi) {
        commands.put(GET_FOOD_COMMAND, new GetFoodsCommand(FoodsCache.getInstance(), FoodBarcodesCache.getInstance(), foodApi));
        commands.put(GET_FOOD_REPORT_COMMAND, new GetFoodReportCommand(FoodReportsCache.getInstance(), foodApi));
        commands.put(GET_FOOD_BY_BARCODE_COMMAND, new GetFoodByBarcodeCommand(FoodBarcodesCache.getInstance()));
    }
}
