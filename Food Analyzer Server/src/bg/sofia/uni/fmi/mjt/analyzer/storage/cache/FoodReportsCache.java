package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.IntRetriever;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.KeyRetriever;

import java.io.File;
import java.nio.file.Path;

/**
 * A singleton object used to cache {@code FoodReport} objects by their id as an Integer key
 */

public class FoodReportsCache extends AbstractCache<Integer, FoodReport> {

    private static final int DEFAULT_CAPACITY = 1000;
    private static final String DEFAULT_PATH = "resources" + File.separator + "food-reports";
    private static final FoodReportsCache instance = initializeInstance();

    private FoodReportsCache(Path diskPath, KeyRetriever<Integer> keyRetriever, int memoryCapacity) {
        super(diskPath, keyRetriever, memoryCapacity);
    }

    public static FoodReportsCache getInstance() {
        return instance;
    }

    private static FoodReportsCache initializeInstance() {
        return new FoodReportsCache(Path.of(DEFAULT_PATH), new IntRetriever(), DEFAULT_CAPACITY);
    }
}
