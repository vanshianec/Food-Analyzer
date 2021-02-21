package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.Food;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.KeyRetriever;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.StringRetriever;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * A singleton object used to cache a list of {@code Food} objects by their name as a String key
 */

public class FoodsCache extends AbstractCache<String, List<Food>> {

    private static final int DEFAULT_CAPACITY = 1000;
    private static final String DEFAULT_PATH = "resources" + File.separator + "foods";
    private static final FoodsCache instance = initializeInstance();

    private FoodsCache(Path diskPath, KeyRetriever<String> keyRetriever, int memoryCapacity) {
        super(diskPath, keyRetriever, memoryCapacity);
    }

    public static FoodsCache getInstance() {
        return instance;
    }

    private static FoodsCache initializeInstance() {
        return new FoodsCache(Path.of(DEFAULT_PATH), new StringRetriever(), DEFAULT_CAPACITY);
    }
}
