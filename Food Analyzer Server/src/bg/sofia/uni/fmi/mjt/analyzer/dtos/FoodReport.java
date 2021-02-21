package bg.sofia.uni.fmi.mjt.analyzer.dtos;

import java.io.Serializable;
import java.util.Objects;

public class FoodReport implements Serializable {

    private static final long serialVersionUID = -69418060182522803L;

    private String description;
    private String ingredients;
    private LabelNutrients labelNutrients;

    public FoodReport(String description, String ingredients, LabelNutrients labelNutrients) {
        this.description = description;
        this.ingredients = ingredients;
        this.labelNutrients = labelNutrients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public LabelNutrients getLabelNutrients() {
        return labelNutrients;
    }

    public void setLabelNutrients(LabelNutrients labelNutrients) {
        this.labelNutrients = labelNutrients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FoodReport that = (FoodReport) o;
        return Objects.equals(description, that.description)
                && Objects.equals(ingredients, that.ingredients)
                && Objects.equals(labelNutrients, that.labelNutrients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, ingredients, labelNutrients);
    }

    @Override
    public String toString() {
        return String.format("Description = %s%nIngredients = %s%nNutrition = %s",
                description, ingredients, labelNutrients);
    }
}
