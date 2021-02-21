package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.analyzer.storage.HeapStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import stubs.FoodServiceStub;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GetFoodReportCommandTest {

    private static final int STORAGE_SIZE = 2;
    private static final Integer STORED_FOOD_ID = 1;
    private static final FoodReport STORED_FOOD = new FoodReport("Some food", "All of them", null);

    private Storage<Integer, FoodReport> foodReportByIdStorage;
    private FoodService foodServiceStub;
    private Command command;

    @Before
    public void initialize() {
        foodReportByIdStorage = new HeapStorage<>(STORAGE_SIZE);
        foodReportByIdStorage.save(STORED_FOOD_ID, STORED_FOOD);
        foodServiceStub = new FoodServiceStub();
        command = new GetFoodReportCommand(foodReportByIdStorage, foodServiceStub);
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
        command.execute("1", "2");
    }

    @Test(expected = InvalidCommandArgumentException.class)
    public void testExecuteWithNoArgumentsCount() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute();
    }

    @Test
    public void testExecuteWithExistingFoodReportInStorage() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        String expected = STORED_FOOD.toString();
        String actual = command.execute(String.valueOf(STORED_FOOD_ID));
        assertEquals("Food report existing in the storage should match those returned by the command",
                expected, actual);
    }

    @Test
    public void testExecuteWithFoodsMissingFromTheStorage() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        int foodId = -1;
        String expected = foodServiceStub.getFoodReport(foodId).toString();
        String actual = command.execute(String.valueOf(foodId));
        assertEquals("Food reports missing in the storage should be returned from the service",
                expected, actual);
    }

    @Test(expected = FoodServiceException.class)
    public void testExecuteWithFoodsMissingFromTheStorageAndService() throws InvalidCommandArgumentException, IOException, FoodServiceException {
        command.execute("-2");
    }

    @Test
    public void testFoodsStorageShouldStoreFoodsReturnedFromTheService() throws FoodServiceException, InvalidCommandArgumentException, IOException {
        int id = -1;
        /* since '-1' is missing in the storage it will be retrieved from the stub service
         *  and it should be stored in the foods storage */
        command.execute(String.valueOf(id));
        FoodReport foodReportFromStorage = foodReportByIdStorage.get(id);
        FoodReport expected = foodServiceStub.getFoodReport(id);
        assertEquals("The food report returned from the service and the one stored in the storage should match",
                foodReportFromStorage, expected);
    }
}
