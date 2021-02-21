package bg.sofia.uni.fmi.mjt.analyzer.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * An object used to store values based on a key in a heap memory
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 */

public class HeapStorage<K, V> implements BoundedStorage<K, V> {

    private static final int INITIAL_SIZE = 0;
    private static final String INVALID_CAPACITY_MESSAGE = "Capacity should be a positive number";

    private Map<K, V> storage;
    private int size;
    private int capacity;

    /**
     * Constructs a new {@code HeapStorage} with a given capacity
     *
     * @param capacity The maximum number of elements which the storage can hold
     */

    public HeapStorage(int capacity) {
        if (capacity <= INITIAL_SIZE) {
            throw new IllegalArgumentException(INVALID_CAPACITY_MESSAGE);
        }

        storage = new HashMap<>();
        size = INITIAL_SIZE;
        this.capacity = capacity;
    }

    @Override
    public void save(K key, V value) {
        if (!isFull()) {
            storage.put(key, value);
            size++;
        }
    }

    @Override
    public V get(K key) {
        return storage.get(key);
    }

    @Override
    public boolean isFull() {
        return size >= capacity;
    }
}
