package bg.sofia.uni.fmi.mjt.analyzer.command;

import org.junit.BeforeClass;
import org.junit.Test;
import stubs.FoodServiceStub;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CommandFactoryTest {

    private static CommandFactory commandFactory;
    private static final String GET_FOOD_COMMAND = "get-food";
    private static final String GET_FOOD_REPORT_COMMAND = "get-food-report";
    private static final String GET_FOOD_BY_BARCODE_COMMAND = "get-food-by-barcode";

    @BeforeClass
    public static void setUp() {
        commandFactory = new CommandFactory(new FoodServiceStub());
    }

    @Test
    public void testGetCommandForFoods() {
        Command result = commandFactory.getCommand(GET_FOOD_COMMAND);
        assertTrue("GetFoodsCommand instance should be returned from the get-food command",
                result instanceof GetFoodsCommand);
    }

    @Test
    public void testGetCommandForFoodReport() {
        Command result = commandFactory.getCommand(GET_FOOD_REPORT_COMMAND);
        assertTrue("GetFoodReportCommand instance should be returned from the get-food-report command",
                result instanceof GetFoodReportCommand);
    }

    @Test
    public void testGetCommandForFoodBarcodes() {
        Command result = commandFactory.getCommand(GET_FOOD_COMMAND);
        assertTrue("GetFoodsCommand instance should be returned from the get-food-by-barcode command",
                result instanceof GetFoodsCommand);
    }

    @Test
    public void testGetInvalidCommand() {
        assertNull("Command result should be null when a non existent command is required",
                commandFactory.getCommand("random command"));
        assertNull("Command result should be null when a null name is passed",
                commandFactory.getCommand(null));
    }
}
