package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.BarcodeReadingException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.ImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.converters.BarcodeConverter;

/**
 * An command object used to execute a command which returns a String representation
 * of a {@code Food} objects
 */

public class GetFoodByBarcodeCommand extends AbstractCommand<String, Food> {

    private static final int MIN_ARGUMENTS_COUNT = 1;
    private static final int MAX_ARGUMENTS_COUNT = 2;
    private static final String IMG_PREFIX = "--img=";
    private static final String CODE_PREFIX = "--code=";
    private static final String INVALID_ARGUMENTS_COUNT = "Invalid arguments count! Expected: between %d and %d arguments.";
    private static final String INVALID_CODE_ARGUMENT = "Code argument should contain only digits!";
    private static final String UNEXPECTED_ARGUMENTS = "Expected at least one image or code argument!";
    private static final String NO_SUCH_FOOD_WITH_BARCODE = "There is no such food found with barcode: '%s'";

    /**
     * Constructs a new {@code GetFoodByBarcodeCommand} with a given foods by barcodes storage
     *
     * @param foodsByBarcodeStorage The storage which will be used to store and retrieve {@code Food} objects by their barcodes
     */

    public GetFoodByBarcodeCommand(Storage<String, Food> foodsByBarcodeStorage) {
        super(foodsByBarcodeStorage);
    }

    @Override
    public String execute(String... arguments) throws InvalidCommandArgumentException, FoodNotFoundException {
        validateArgumentsCount(arguments);
        return getBrandedFood(arguments).toString().strip();
    }

    private void validateArgumentsCount(String... arguments) throws InvalidCommandArgumentException {
        if (arguments == null || !areArgumentsCountValid(arguments.length)) {
            throw new InvalidCommandArgumentException(INVALID_ARGUMENTS_COUNT
                    .formatted(MIN_ARGUMENTS_COUNT, MAX_ARGUMENTS_COUNT));
        }
    }

    private boolean areArgumentsCountValid(int argumentsCount) {
        return argumentsCount >= MIN_ARGUMENTS_COUNT && argumentsCount <= MAX_ARGUMENTS_COUNT;
    }

    private Food getBrandedFood(String... arguments) throws InvalidCommandArgumentException, FoodNotFoundException {
        String codeArgument = null;
        String imageArgument = null;

        for (String argument : arguments) {
            if (argument.startsWith(CODE_PREFIX)) {
                codeArgument = argument.substring(CODE_PREFIX.length());
            } else if (argument.startsWith(IMG_PREFIX)) {
                imageArgument = argument.substring(IMG_PREFIX.length());
            }
        }

        String code = getBrandedFoodCode(codeArgument, imageArgument);
        return getBrandedFoodFromCache(code);
    }

    private String getBrandedFoodCode(String codeArgument, String imageArgument) throws InvalidCommandArgumentException {
        if (codeArgument != null) {
            validateCodeArgument(codeArgument);
            return codeArgument;
        }

        if (imageArgument != null) {
            try {
                return BarcodeConverter.getGtinUpcFromImage(imageArgument);
            } catch (BarcodeReadingException | ImageNotFoundException | InvalidPathException e) {
                throw new InvalidCommandArgumentException(e.getMessage());
            }
        }

        throw new InvalidCommandArgumentException(UNEXPECTED_ARGUMENTS);
    }

    private void validateCodeArgument(String codeArgument) throws InvalidCommandArgumentException {
        if (!codeArgument.chars().allMatch(Character::isDigit)) {
            throw new InvalidCommandArgumentException(INVALID_CODE_ARGUMENT);
        }
    }

    private Food getBrandedFoodFromCache(String barcode) throws FoodNotFoundException {
        Food food = storage.get(barcode);
        if (food == null) {
            throw new FoodNotFoundException(NO_SUCH_FOOD_WITH_BARCODE.formatted(barcode));
        }

        return food;
    }
}
