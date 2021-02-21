package bg.sofia.uni.fmi.mjt.analyzer.api;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodsList;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodApiException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FoodHttpApiTest {

    private static String foodsJson;
    private static String foodReportJson;
    private static List<Food> foodsResponseList;
    private static FoodReport foodReportResponse;

    @Mock
    private HttpClient foodApiClientMock;

    @Mock
    private HttpResponse<String> httpFoodResponseMock;

    private static FoodService foodApi;

    @BeforeClass
    public static void setUpClass() {
        Gson gson = new Gson();
        foodsResponseList = new ArrayList<>() {{
            add(new Food(1, "BRANDED", "pizza", "0041423452"));
            add(new Food(2, "other", "ice cream", "123456678"));
        }};
        foodReportResponse = new FoodReport("some description", "some ingredients", null);

        foodReportJson = gson.toJson(foodReportResponse);
        foodsJson = gson.toJson(new FoodsList(foodsResponseList));
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        when(foodApiClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpFoodResponseMock);
        foodApi = new FoodHttpApi(foodApiClientMock);
    }

    @Test
    public void testGetFoodsWithValidFoodName() throws FoodServiceException {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpFoodResponseMock.body()).thenReturn(foodsJson);

        var resultList = foodApi.getFoods("some food");

        assertEquals("Incorrect foods response for a valid food name", foodsResponseList, resultList);
    }

    @Test
    public void testGetFoodReportWithValidFoodId() throws FoodServiceException {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpFoodResponseMock.body()).thenReturn(foodReportJson);

        FoodReport result = foodApi.getFoodReport(1);

        assertEquals("Incorrect food report response for a valid food id", foodReportResponse, result);
    }

    @Test(expected = FoodNotFoundException.class)
    public void testGetFoodsWithNonExistentFoodName() throws FoodServiceException {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        foodApi.getFoods("some missing food");
    }

    @Test(expected = FoodNotFoundException.class)
    public void testGetFoodReportWithNonExistentFoodId() throws FoodServiceException {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        foodApi.getFoodReport(-1);
    }

    @Test
    public void testGetFoodsServerError() {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAVAILABLE);

        try {
            foodApi.getFoods("some foods");
        } catch (Exception e) {
            assertEquals("FoodApiException should be thrown if the foods could not be retrieved",
                    FoodApiException.class, e.getClass());
            assertNotEquals("Improper use of FoodNotFoundException! It should be thrown only if the food is not found",
                    FoodNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void testGetFoodReportServerError() {
        when(httpFoodResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAVAILABLE);

        try {
            foodApi.getFoodReport(1);
        } catch (Exception e) {
            assertEquals("FoodApiException should be thrown if the food report could not be retrieved",
                    FoodApiException.class, e.getClass());
            assertNotEquals("Improper use of FoodNotFoundException! It should be thrown only if a food with the given id doesn't exist",
                    FoodNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void testGetFoodsHttpClientIOExceptionIsWrapped() throws IOException, InterruptedException {
        IOException expectedException = new IOException();
        when(foodApiClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(expectedException);

        try {
            foodApi.getFoods("some foods");
        } catch (Exception actualException) {
            assertEquals(
                    "FoodApiException should properly wrap the causing IOException",
                    expectedException, actualException.getCause());
        }
    }

    @Test
    public void testGetFoodsHttpClientInterruptedExceptionIsWrapped() throws IOException, InterruptedException {
        InterruptedException expectedException = new InterruptedException();
        when(foodApiClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(expectedException);

        try {
            foodApi.getFoods("some foods");
        } catch (Exception actualException) {
            assertEquals(
                    "FoodApiException should properly wrap the causing InterruptedException",
                    expectedException, actualException.getCause());
        }
    }
}
