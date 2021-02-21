package bg.sofia.uni.fmi.mjt.analyzer.api;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodsList;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodApiException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodNotFoundException;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * An object used to send http requests to a food based api
 * and convert the responses to food type objects
 */

public class FoodHttpApi implements FoodService {

    private static final String API_KEY = "hvvJM4Ml1yQfLiwABKO1bkYZbwE6YqTKPeUccWDP";
    private static final String SCHEME = "https";
    private static final String HOST = "api.nal.usda.gov";
    private static final String FOOD_PATH = "/fdc/v1/foods/search";
    private static final String FOOD_QUERY = "api_key=%s&query=%s&requireAllWords=true";
    private static final String FOOD_REPORT_PATH = "/fdc/v1/food/%d";
    private static final String FOOD_REPORT_QUERY = "api_key=%s";
    private static final String NON_EXISTING_FOOD_FDC_ID = "There is no such food with fdcId: '%d'";
    private static final String NON_EXISTING_FOOD_NAME = "There is no such food: '%s'";
    private static final String UNEXPECTED_RESPONSE_CODE = "Unexpected response code from food service";
    private static final String FAILED_TO_RETRIEVE_FOOD = "Could not retrieve food";

    private HttpClient foodHttpClient;
    private String apiKey;
    private Gson gson;

    /**
     * Constructs a {@code FoodHttpApi} object with the provided httpClient and a default api key
     *
     * @param foodHttpClient The client object which will send the requests
     */

    public FoodHttpApi(HttpClient foodHttpClient) {
        this.foodHttpClient = foodHttpClient;
        this.apiKey = API_KEY;
        this.gson = new Gson();
    }

    /**
     * Constructs a {@code FoodHttpApi} object with the provided httpClient and api key
     *
     * @param foodHttpClient The client object which will send the requests
     * @param apiKey         The key for the food api central
     */

    public FoodHttpApi(HttpClient foodHttpClient, String apiKey) {
        this.foodHttpClient = foodHttpClient;
        this.apiKey = apiKey;
        this.gson = new Gson();
    }

    /**
     * Fetches a list of foods with a given food name by sending an http request to a food based api
     *
     * @param foodName The name of the foods to be fetched
     * @return A list of all foods which match the given food name
     * @throws FoodApiException If an error occurs while trying to retrieve the food information from the api
     */

    @Override
    public List<Food> getFoods(String foodName) throws FoodApiException {
        HttpResponse<String> response = getHttpResponse(FOOD_PATH, FOOD_QUERY.formatted(apiKey, foodName));
        validateStatusCode(response.statusCode(), NON_EXISTING_FOOD_NAME.formatted(foodName));
        FoodsList foodsList = gson.fromJson(response.body(), FoodsList.class);
        List<Food> foodsResult = foodsList.getFoods();
        /* Check if the list is empty in case the api
         returns an empty list instead of 404 status code*/
        if (foodsResult.isEmpty()) {
            throw new FoodNotFoundException(NON_EXISTING_FOOD_NAME.formatted(foodName));
        }

        return foodsList.getFoods();
    }

    /**
     * Brings a food report of a food with a given id by sending an http request to a food based api
     *
     * @param fdcId The id of the food from which a report will be returned
     * @return A report about the food
     * @throws FoodApiException If an error occurs while trying to retrieve the food information from the api
     */

    @Override
    public FoodReport getFoodReport(int fdcId) throws FoodApiException {
        HttpResponse<String> response = getHttpResponse(FOOD_REPORT_PATH.formatted(fdcId), FOOD_REPORT_QUERY.formatted(apiKey));
        validateStatusCode(response.statusCode(), NON_EXISTING_FOOD_FDC_ID.formatted(fdcId));
        return gson.fromJson(response.body(), FoodReport.class);
    }

    private HttpResponse<String> getHttpResponse(String uriPath, String uriQuery) throws FoodApiException {
        HttpResponse<String> response;
        try {
            URI uri = createURI(uriPath, uriQuery);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            response = foodHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new FoodApiException(FAILED_TO_RETRIEVE_FOOD, e);
        }
        return response;
    }

    private URI createURI(String path, String query) throws URISyntaxException {
        return new URI(SCHEME, HOST, path, query, null);
    }

    private void validateStatusCode(int statusCode, String recourseNotFoundMessage) throws FoodApiException, FoodNotFoundException {
        if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new FoodNotFoundException(recourseNotFoundMessage);
        } else if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new FoodApiException(UNEXPECTED_RESPONSE_CODE);
        }
    }
}
