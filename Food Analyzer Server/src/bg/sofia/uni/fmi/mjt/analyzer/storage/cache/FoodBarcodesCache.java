package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.KeyRetriever;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.StringRetriever;

import java.io.File;
import java.nio.file.Path;

/**
 * A singleton object used to cache {@code Food} objects by their barcode as a String key
 */

public class FoodBarcodesCache extends AbstractCache<String, Food> {

    private static final int DEFAULT_CAPACITY = 1000;
    private static final String DEFAULT_PATH = "resources" + File.separator + "food-barcodes";
    private static final FoodBarcodesCache instance = initializeInstance();

    private FoodBarcodesCache(Path diskPath, KeyRetriever<String> keyRetriever, int memoryCapacity) {
        super(diskPath, keyRetriever, memoryCapacity);
    }

    public static FoodBarcodesCache getInstance() {
        return instance;
    }

    private static FoodBarcodesCache initializeInstance() {
        return new FoodBarcodesCache(Path.of(DEFAULT_PATH), new StringRetriever(), DEFAULT_CAPACITY);
    }
}


