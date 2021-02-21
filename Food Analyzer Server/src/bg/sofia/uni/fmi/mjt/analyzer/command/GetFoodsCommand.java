package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;

import java.util.List;

/**
 * An command object used to execute a command which returns a String representation
 * of a {@code List} of {@code Food} objects
 */

public class GetFoodsCommand extends AbstractServiceCommand<String, List<Food>> {

    private static final int ZERO_ARGUMENTS = 0;
    private static final String INVALID_ARGUMENTS_COUNT = "At least one argument is required";
    private static final String WHITESPACE = " ";

    private Storage<String, Food> barcodesCache;

    /**
     * Constructs a new {@code GetFoodCommand} with a given foods and barcodes storages and a food service
     *
     * @param foodsStorage    The storage which will be used to store and retrieve a {@code List} of {@code Food} objects by their name
     * @param barcodesStorage The storage which will be used to store and retrieve {@code Food} objects by their barcodes
     * @param foodService   The service which will be used to retrieve {@code Food} data if no such data is found in the storages
     */

    public GetFoodsCommand(Storage<String, List<Food>> foodsStorage, Storage<String, Food> barcodesStorage, FoodService foodService) {
        super(foodsStorage, foodService);
        this.barcodesCache = barcodesStorage;
    }

    @Override
    public String execute(String... arguments) throws FoodServiceException, InvalidCommandArgumentException {
        validateArguments(arguments);

        String foodName = String.join(WHITESPACE, arguments);
        List<Food> foods = storage.get(foodName);
        if (foods == null) {
            foods = foodService.getFoods(foodName);
            saveFoodsWithBarcodesInBarcodesCache(foods);
            storage.save(foodName, foods);
        }

        return foods.toString();
    }

    private void saveFoodsWithBarcodesInBarcodesCache(List<Food> foods) {
        foods.forEach(f -> {
            if (f.getGtinUpc() != null) {
                barcodesCache.save(f.getGtinUpc(), f);
            }
        });
    }

    private void validateArguments(String... arguments) throws InvalidCommandArgumentException {
        if (arguments == null || arguments.length == ZERO_ARGUMENTS) {
            throw new InvalidCommandArgumentException(INVALID_ARGUMENTS_COUNT);
        }
    }
}
