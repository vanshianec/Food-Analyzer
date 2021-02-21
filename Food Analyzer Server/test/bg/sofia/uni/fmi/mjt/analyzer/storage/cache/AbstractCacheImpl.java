package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.storage.BoundedStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.UnboundedStorage;

public class AbstractCacheImpl extends AbstractCache<Integer, String> {
    public AbstractCacheImpl(UnboundedStorage<Integer, String> diskStorage, BoundedStorage<Integer, String> heapStorage) {
        super(diskStorage, heapStorage);
    }
}
