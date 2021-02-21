package stubs;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.FoodNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FoodServiceStub implements FoodService {

    @Override
    public List<Food> getFoods(String foodName) throws FoodNotFoundException {
        if (foodName.equals("stub")) {
            return new ArrayList<>() {{
                add(new Food(1, "STUB", "STUB", null));
                add(new Food(2, "STUB2", "STUB2", "12345678"));
            }};
        }

        throw new FoodNotFoundException("");
    }

    @Override
    public FoodReport getFoodReport(int id) throws FoodNotFoundException {
        if (id == -1) {
            return new FoodReport("stub", "stub", null);
        }

        throw new FoodNotFoundException("");
    }
}
