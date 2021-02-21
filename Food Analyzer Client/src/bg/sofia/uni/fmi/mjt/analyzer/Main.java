package bg.sofia.uni.fmi.mjt.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final String SERVER_CLOSED_MESSAGE = "The server is closed, please try again later";

    public static void main(String[] args) throws IOException {
        Client client = new FoodAnalyzerClient(7000, "localhost");
        client.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input = bufferedReader.readLine();
        while (!input.equalsIgnoreCase("quit")) {
            String response = client.sendMessage(input);
            System.out.println(response);

            if (response.equals(SERVER_CLOSED_MESSAGE)) {
                client.stop();
                break;
            }

            input = bufferedReader.readLine();
        }
    }
}
