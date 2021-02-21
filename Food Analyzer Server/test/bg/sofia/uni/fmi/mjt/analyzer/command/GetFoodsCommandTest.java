package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.HeapStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import stubs.FoodServiceStub;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GetFoodsCommandTest {

    private static final int STORAGE_SIZE = 2;
    private static final String STORED_FOOD_NAME = "pizza";
    private static final Food FIRST_STORED_FOOD = new Food(1, "BRANDED", "First pizza", "123");
    private static final Food SECOND_STORED_FOOD = new Food(2, "NOT-BRANDED", "Second pizza", null);
    private static final List<Food> STORED_FOODS = new ArrayList<>() {{
        add(FIRST_STORED_FOOD);
        add(SECOND_STORED_FOOD);
    }};
    private static final Food FOOD_WITH_UPC_RETURNED_FROM_STUB = new Food(2, "STUB2", "STUB2", "12345678");

    private Storage<String, List<Food>> foodsByNamesStorage;
    private Storage<String, Food> foodByBarcodeStorage;
    private FoodService foodServiceStub;
    private Command command;

    @Before
    public void initialize() {
        foodsByNamesStorage = new HeapStorage<>(STORAGE_SIZE);
        foodsByNamesStorage.save(STORED_FOOD_NAME, STORED_FOODS);
        foodByBarcodeStorage = new HeapStorage<>(STORAGE_SIZE);
        foodServiceStub = new FoodServiceStub();
        command = new GetFoodsCommand(foodsByNamesStorage, foodByBarcodeStorage, foodServiceStub);
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithNullArguments() throws InvalidCommandArgumentException, FoodServiceException {
        command.execute(null);
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithInvalidArgumentsCount() throws InvalidCommandArgumentException, FoodServiceException {
        command.execute();
    }

    @Test
    public void testExecuteWithExistingFoodsInStorage() throws InvalidCommandArgumentException, FoodServiceException {
        String expected = STORED_FOODS.toString();
        String actual = command.execute(STORED_FOOD_NAME);
        assertEquals("Foods existing in the storage should match those returned by the command",
                expected, actual);
    }

    @Test
    public void testExecuteWithFoodsMissingFromTheStorage() throws InvalidCommandArgumentException, FoodServiceException {
        String foodName = "stub";
        String expected = foodServiceStub.getFoods(foodName).toString();
        String actual = command.execute(foodName);
        assertEquals("Foods missing in the storage should be returned from the service",
                expected, actual);
    }

    @Test(expected = FoodServiceException.class)
    public void testExecuteWithFoodsMissingFromTheStorageAndService() throws InvalidCommandArgumentException, FoodServiceException {
        command.execute("");
    }

    @Test
    public void testFoodsStorageShouldStoreFoodsReturnedFromTheService() throws FoodServiceException, InvalidCommandArgumentException {
        String foodName = "stub";
        /* since 'stub' is missing in the storage it will be retrieved from the stub service
         *  and it should be stored in the foods storage */
        command.execute(foodName);
        List<Food> foodsFromStorage = foodsByNamesStorage.get(foodName);
        List<Food> expected = foodServiceStub.getFoods(foodName);
        assertEquals("The food returned from the service and the one stored in the storage should match",
                foodsFromStorage, expected);
    }

    @Test
    public void testBarcodesStorageStorageShouldStoreFoodsWithUpcCodeFromTheService() throws FoodServiceException, InvalidCommandArgumentException {
        String foodName = "stub";
        /* since 'stub' is missing in the storage it will be retrieved from the stub service
         *  which contains one food with a upc code so that one food should be stored in the barcodes storage */
        command.execute(foodName);
        Food foodFromStorage = foodByBarcodeStorage.get(FOOD_WITH_UPC_RETURNED_FROM_STUB.getGtinUpc());
        assertEquals("The food returned from the service and the one stored in the storage should match",
                foodFromStorage, FOOD_WITH_UPC_RETURNED_FROM_STUB);
        assertEquals("The upc codes of the food returned from the service and the one stored in the storage should match",
                foodFromStorage.getGtinUpc(), FOOD_WITH_UPC_RETURNED_FROM_STUB.getGtinUpc());
    }
}
