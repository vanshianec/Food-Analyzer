package bg.sofia.uni.fmi.mjt.analyzer.storage;

/**
 * An object which can store unbounded amounts of values based on a key
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 * @see DiskStorage
 */

public interface UnboundedStorage<K, V> extends Storage<K, V> {

    /**
     * Creates a {@code BoundedStorage} object from the current {@code UnboundedStorage}
     * object with a given limit of the returned elements
     *
     * @param limit The size of the returned {@code BoundedStorage}
     * @return {@code BoundedStorage} object containing as much values of the current {@code UnboundedStorage}
     * object until the limit is reached
     */

    BoundedStorage<K, V> load(int limit);
}
