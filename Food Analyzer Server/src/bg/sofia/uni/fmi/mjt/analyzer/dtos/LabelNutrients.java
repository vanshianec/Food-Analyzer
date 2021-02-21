package bg.sofia.uni.fmi.mjt.analyzer.dtos;

import java.io.Serializable;
import java.util.Objects;

public class LabelNutrients implements Serializable {

    private static final long serialVersionUID = -6822928269540110632L;

    private Nutrient fat;
    private Nutrient carbohydrates;
    private Nutrient fiber;
    private Nutrient protein;
    private Nutrient calories;

    public LabelNutrients(Nutrient fat, Nutrient carbohydrates, Nutrient fiber, Nutrient protein, Nutrient calories) {
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.protein = protein;
        this.calories = calories;
    }

    public Nutrient getFat() {
        return fat;
    }

    public void setFat(Nutrient fat) {
        this.fat = fat;
    }

    public Nutrient getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Nutrient carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Nutrient getFiber() {
        return fiber;
    }

    public void setFiber(Nutrient fiber) {
        this.fiber = fiber;
    }

    public Nutrient getProtein() {
        return protein;
    }

    public void setProtein(Nutrient protein) {
        this.protein = protein;
    }

    public Nutrient getCalories() {
        return calories;
    }

    public void setCalories(Nutrient calories) {
        this.calories = calories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LabelNutrients that = (LabelNutrients) o;
        return Objects.equals(fat, that.fat)
                && Objects.equals(carbohydrates, that.carbohydrates)
                && Objects.equals(fiber, that.fiber)
                && Objects.equals(protein, that.protein)
                && Objects.equals(calories, that.calories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fat, carbohydrates, fiber, protein, calories);
    }

    @Override
    public String toString() {
        return String.format("calories - %s, carbohydrates - %s, protein - %s, fat - %s, fiber -  %s",
                calories, carbohydrates, protein, fat, fiber);
    }
}
