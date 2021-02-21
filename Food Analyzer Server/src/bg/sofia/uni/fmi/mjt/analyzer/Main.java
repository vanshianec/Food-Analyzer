package bg.sofia.uni.fmi.mjt.analyzer;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.api.FoodHttpApi;
import bg.sofia.uni.fmi.mjt.analyzer.server.FoodAnalyzerServer;
import bg.sofia.uni.fmi.mjt.analyzer.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static class ServerThread implements Runnable {

        private Server server;

        public ServerThread(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            server.start();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 7000;
        String host = "localhost";
        FoodService foodApi = new FoodHttpApi(HttpClient.newBuilder().build());
        Server server = new FoodAnalyzerServer(port, host, foodApi);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String input = bufferedReader.readLine();
        while (!input.equalsIgnoreCase("quit")) {
            if (input.equalsIgnoreCase("stop")) {
                server.stop();
            } else if (input.equalsIgnoreCase("start")) {
                executor.execute(new ServerThread(server));
            }

            input = bufferedReader.readLine();
        }

        executor.shutdown();
    }
}
