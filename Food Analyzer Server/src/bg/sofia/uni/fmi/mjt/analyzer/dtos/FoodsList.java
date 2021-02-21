package bg.sofia.uni.fmi.mjt.analyzer.dtos;

import java.util.List;

public class FoodsList {

    private List<Food> foods;

    public FoodsList(List<Food> foods) {
        this.foods = foods;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}
