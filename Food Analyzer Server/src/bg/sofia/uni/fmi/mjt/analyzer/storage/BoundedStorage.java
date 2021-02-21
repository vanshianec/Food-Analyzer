package bg.sofia.uni.fmi.mjt.analyzer.storage;

/**
 * An object which can store bounded(limited) amounts of values based on a key
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 * @see HeapStorage
 */

public interface BoundedStorage<K, V> extends Storage<K, V> {

    /**
     * Checks whether the current storage is full or not
     *
     * @return True if the storage is full and False otherwise
     */

    boolean isFull();

}
