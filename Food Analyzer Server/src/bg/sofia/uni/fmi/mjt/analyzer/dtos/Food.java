package bg.sofia.uni.fmi.mjt.analyzer.dtos;

import java.io.Serializable;
import java.util.Objects;

public class Food implements Serializable {

    private static final long serialVersionUID = 9054456418123225003L;

    private int fdcId;
    private String dataType;
    private String description;
    private String gtinUpc;

    public Food(int fdcId, String dataType, String description, String gtinUpc) {
        this.fdcId = fdcId;
        this.dataType = dataType;
        this.description = description;
        this.gtinUpc = gtinUpc;
    }

    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGtinUpc() {
        return gtinUpc;
    }

    public void setGtinUpc(String gtinUpc) {
        this.gtinUpc = gtinUpc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Food food = (Food) o;
        return fdcId == food.fdcId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fdcId);
    }

    @Override
    public String toString() {
        return String.format("%nDescription = %s, fdcId = %d",
                description, fdcId);
    }
}
