package bg.sofia.uni.fmi.mjt.analyzer.storage;

/**
 * An object used to store values based on a key
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 */

public interface Storage<K, V> {

    /**
     * Saves the value object in the storage by its key
     *
     * @param key   The key by which the object will be identified in the storage
     * @param value The value object which will be stored in the storage
     */

    void save(K key, V value);

    /**
     * Retrieves the value object from the storage based on its key object
     *
     * @param key The key object by which the value object will be retrieved
     * @return The value object
     */

    V get(K key);
}
