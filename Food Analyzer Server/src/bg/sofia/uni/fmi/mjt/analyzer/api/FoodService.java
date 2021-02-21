package bg.sofia.uni.fmi.mjt.analyzer.api;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodServiceException;

import java.util.List;

/**
 * An object used to retrieve information about food based objects
 */

public interface FoodService {

    /**
     * Fetches a list of foods with a given food name
     *
     * @param foodName The name of the foods to be fetched
     * @return A list of all foods which match the given food name
     * @throws FoodServiceException If an error occurs while trying to retrieve the food information
     */

    List<Food> getFoods(String foodName) throws FoodServiceException;

    /**
     * Brings a food report of a food with a given id
     *
     * @param id The id of the food from which a report will be returned
     * @return A report about the food
     * @throws FoodServiceException If an error occurs while trying to retrieve the food information
     */

    FoodReport getFoodReport(int id) throws FoodServiceException;
}
