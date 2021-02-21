package bg.sofia.uni.fmi.mjt.analyzer.server;

import bg.sofia.uni.fmi.mjt.analyzer.client.Client;
import bg.sofia.uni.fmi.mjt.analyzer.client.FoodAnalyzerClient;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import stubs.FoodServiceStub;

import static org.junit.Assert.assertEquals;

/* Testing the FoodAnalyzerServer and FoodAnalyzerClient integration */

public class FoodAnalyzerServerTest {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static final String GET_FOOD_REPORT_COMMAND = "get-food-report";
    private static final String INVALID_COMMAND_RESPONSE_MESSAGE = """
            There is no such command : '%s' \
            Supported commands are: 'get-food [food name]', 'get-food-report [food id]', \
            'get-food-by-barcode [--img=UPC_IMAGE_FOLDER_PATH or --code=FOOD_UPC_CODE]'\
            """;
    private static String foodReportServerResponse;
    private static Server server;
    private static Client client;

    @BeforeClass
    public static void initialize() throws InterruptedException {
        server = new FoodAnalyzerServer(SERVER_PORT, SERVER_HOST, new FoodServiceStub());
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        /* wait a bit for the server to start */
        Thread.sleep(1000);
        client = new FoodAnalyzerClient(SERVER_PORT, SERVER_HOST);
        client.start();
        FoodReport foodReportFromStubService = new FoodReport("stub", "stub", null);
        foodReportServerResponse = foodReportFromStubService.toString();
    }

    @Test
    public void testServerResponseWhenAValidCommandIsSend() {
        String foodIdFromStub = "-1";
        String response = client.sendMessage(GET_FOOD_REPORT_COMMAND + " " + foodIdFromStub);
        assertEquals("The server should send a response message to the client when the command is valid",
                foodReportServerResponse, response);
    }

    @Test
    public void testServerResponseWhenAnInvalidCommandIsSend() {
        String invalidCommand = "invalid-command";
        String response = client.sendMessage(invalidCommand);
        assertEquals("The server should send a response message to the client when the command is not valid",
                INVALID_COMMAND_RESPONSE_MESSAGE.formatted(invalidCommand), response);
    }

    @Test
    public void testSendingMessageWithoutEstablishedConnectionWithTheServer() {
        Client notConnectedClient = new FoodAnalyzerClient(SERVER_PORT, SERVER_HOST);
        String expectedResponse = "You are not connected to the server. Please try to reconnect";
        String response = notConnectedClient.sendMessage("some message");
        assertEquals("A notification message should be received when client is not connected to the server",
                expectedResponse, response);
    }

    @Test
    public void testSendingMessageAfterLosingConnectionWithTheServer() {
        Client newClient = new FoodAnalyzerClient(SERVER_PORT, SERVER_HOST);
        newClient.start();
        String foodIdFromStub = "-1";
        String response = newClient.sendMessage(GET_FOOD_REPORT_COMMAND + " " + foodIdFromStub);
        assertEquals("The client message should be sent when a connection with the server is established",
                foodReportServerResponse, response);
        newClient.stop();
        String expectedResponse = "You are not connected to the server. Please try to reconnect";
        response = newClient.sendMessage("i am offline");
        assertEquals("The client should be notified that he is not connected to the server",
                expectedResponse, response);
    }

    @AfterClass
    public static void destroy() {
        server.stop();
    }
}
