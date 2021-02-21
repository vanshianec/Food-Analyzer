package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.HeapStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GetFoodByBarcodeCommandTest {

    private static final int STORAGE_SIZE = 2;
    private static final String STORED_FOOD_UPC_CODE = "025484007109";
    private static final String COMMAND_UPC_CODE_PREFIX = "--code=";
    private static final String COMMAND_IMAGE_PATH_PREFIX = "--img=";
    private static final Food STORED_FOOD = new Food(1, "", "", STORED_FOOD_UPC_CODE);

    private Storage<String, Food> foodByBarcodeStorage;
    private Command command;

    @Before
    public void initialize() {
        foodByBarcodeStorage = new HeapStorage<>(STORAGE_SIZE);
        foodByBarcodeStorage.save(STORED_FOOD_UPC_CODE, STORED_FOOD);
        command = new GetFoodByBarcodeCommand(foodByBarcodeStorage);
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithNullArguments() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute(null);
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithInvalidArguments() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute("invalid argument");
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithInvalidArgumentsCount() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute("1", "2", "3");
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithNoArgumentsCount() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute();
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithInvalidUpcCode() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String invalidCode = "9j39301jfjd";
        command.execute(COMMAND_UPC_CODE_PREFIX + invalidCode);
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithInvalidImagePath() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String invalidPath = "some invalid path @**$";
        command.execute(COMMAND_IMAGE_PATH_PREFIX + invalidPath);
    }

    @Test(expected = FoodNotFoundException.class)
    public void testExecuteWithNonExistingUpcCodeInStorage() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String nonExistingCode = "9999999999";
        command.execute(COMMAND_UPC_CODE_PREFIX + nonExistingCode);
    }

    @Test
    public void testExecuteWithExistingUpcCodeInStorage() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String expected = STORED_FOOD.toString().strip();
        String actual = command.execute(COMMAND_UPC_CODE_PREFIX + STORED_FOOD_UPC_CODE);
        assertEquals("Food with existing upc code in the storage should match those returned by the command",
                expected, actual);
    }

    @Test
    public void testExecuteWithExistingImagePath() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String upcCodeImagePath = "test" + File.separator + "resources"
                + File.separator + "barcode-images" + File.separator + "validImage.gif";
        String expected = STORED_FOOD.toString().strip();
        String actual = command.execute(COMMAND_IMAGE_PATH_PREFIX + upcCodeImagePath);
        assertEquals("Food upc code taken from image which exists in the storage should match those returned by the command",
                expected, actual);
    }

    @Test
    public void testIfUpcImagePathIsIgnoredWhenAnUpcCodeIsAlreadyProvided() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        /* will throw an exception if not ignored by the command */
        String invalidImagePath = "some invalid path$#*";
        String expected = STORED_FOOD.toString().strip();
        String actual = command.execute(COMMAND_IMAGE_PATH_PREFIX + invalidImagePath,
                COMMAND_UPC_CODE_PREFIX + STORED_FOOD_UPC_CODE);
        assertEquals("Image path to upc code should be ignored when an upc code is already provided as an argument",
                expected, actual);
    }
}
