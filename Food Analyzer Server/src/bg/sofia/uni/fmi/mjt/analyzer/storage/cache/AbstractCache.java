package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.storage.DiskStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.BoundedStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.UnboundedStorage;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.KeyRetriever;

import java.nio.file.Path;

/**
 * An object used to store and retrieve values based on a key in a fast way by using
 * heap memory caching until the maximum amount of elements to be stored in the memory is reached
 * and after that elements will be cached in a file. The {@code AbstractCache} object
 * is thread save.
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 * @see FoodBarcodesCache
 * @see FoodReportsCache
 * @see FoodsCache
 */

public abstract class AbstractCache<K, V> implements Storage<K, V> {

    private UnboundedStorage<K, V> diskStorage;
    private BoundedStorage<K, V> heapStorage;

    /**
     * Constructs a new {@code AbstractCache} which will use the provided disk and heap storages
     *
     * @param diskStorage The disk storage which stores values in a file
     * @param heapStorage The heap storage which stores values in the dynamic memory
     */

    protected AbstractCache(UnboundedStorage<K, V> diskStorage, BoundedStorage<K, V> heapStorage) {
        this.diskStorage = diskStorage;
        this.heapStorage = heapStorage;
    }

    /**
     * Constructs a new {@code AbstractCache} which loads all data from the provided file path
     * in its heap memory
     *
     * @param filePath       The path of the file in which the data will be loaded in memory
     * @param keyRetriever   The The retriever which will be used to retrieve the key object
     *                       by which the value is stored in the file based on the file name
     * @param memoryCapacity The maximum number of elements which can be stored  in the heap memory cache
     */

    protected AbstractCache(Path filePath, KeyRetriever<K> keyRetriever, int memoryCapacity) {
        diskStorage = new DiskStorage<>(filePath, keyRetriever);
        heapStorage = diskStorage.load(memoryCapacity);
    }

    @Override
    public synchronized V get(K key) {
        V value = heapStorage.get(key);
        if (value == null && heapStorage.isFull()) {
            return diskStorage.get(key);
        }

        return value;
    }

    @Override
    public synchronized void save(K key, V value) {
        heapStorage.save(key, value);
        diskStorage.save(key, value);
    }
}
