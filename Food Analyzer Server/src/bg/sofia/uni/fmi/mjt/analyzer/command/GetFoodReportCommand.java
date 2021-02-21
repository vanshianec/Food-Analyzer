package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;

/**
 * An command object used to execute a command which returns a String representation
 * of a {@code FoodReport} objects
 */

public class GetFoodReportCommand extends AbstractServiceCommand<Integer, FoodReport> {

    private static final int EXPECTED_ARGUMENTS_COUNT = 1;
    private static final int FDC_ID = 0;
    private static final String INVALID_ARGUMENTS_COUNT = "Invalid arguments count! Expected: %d argument";
    private static final String INVALID_FDC_ID = "Food id should be an integer number!";

    /**
     * Constructs a new {@code GetFoodReportCommand} with a given food reports storage and a food service
     *
     * @param foodReportsStorage The storage which will be used to store and retrieve {@code FoodReport} objects by their ids
     * @param foodService      The service which will be used to retrieve {@code Food} data if no such data is found in the storage
     */

    public GetFoodReportCommand(Storage<Integer, FoodReport> foodReportsStorage, FoodService foodService) {
        super(foodReportsStorage, foodService);
    }

    @Override
    public String execute(String... arguments) throws FoodServiceException, InvalidCommandArgumentException {
        validateArgumentsCount(arguments);

        int fdcId = getFdcIdArgument(arguments);
        FoodReport foodReport = storage.get(fdcId);

        if (foodReport == null) {
            foodReport = foodService.getFoodReport(fdcId);
            storage.save(fdcId, foodReport);
        }

        return foodReport.toString();
    }

    private void validateArgumentsCount(String... arguments) throws InvalidCommandArgumentException {
        if (arguments == null || arguments.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentException(INVALID_ARGUMENTS_COUNT.formatted(EXPECTED_ARGUMENTS_COUNT));
        }
    }

    private int getFdcIdArgument(String... arguments) throws InvalidCommandArgumentException {
        try {
            return Integer.parseInt(arguments[FDC_ID]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException(INVALID_FDC_ID);
        }
    }
}
