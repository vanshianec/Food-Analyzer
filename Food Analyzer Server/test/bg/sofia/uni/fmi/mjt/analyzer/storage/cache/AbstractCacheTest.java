package bg.sofia.uni.fmi.mjt.analyzer.storage.cache;

import bg.sofia.uni.fmi.mjt.analyzer.storage.BoundedStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.DiskStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.HeapStorage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;
import bg.sofia.uni.fmi.mjt.analyzer.storage.UnboundedStorage;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.IntRetriever;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AbstractCacheTest {

    private Storage<Integer, String> cache;
    private BoundedStorage<Integer, String> memoryStorage;
    private UnboundedStorage<Integer, String> diskStorage;
    private static final int HEAP_CAPACITY = 1;
    private static final Path DISK_PATH = Path.of("test", "disk-storage-test-objects", "cache-storage");

    @Before
    public void setUp() {
        memoryStorage = new HeapStorage<>(HEAP_CAPACITY);
        diskStorage = new DiskStorage<>(DISK_PATH, new IntRetriever());
        cache = new AbstractCacheImpl(diskStorage, memoryStorage);
    }

    @Test
    public void testSaveShouldSaveInBothMemoryAndDiskStorages() {
        int key = 1;
        String value = "some value";
        cache.save(key, value);
        assertNotNull("Cache should save its values in memory storage", memoryStorage.get(key));
        assertNotNull("Cache should save its values in disk storage", diskStorage.get(key));
    }

    @Test
    public void testSaveWhenMemoryStorageIsFull() {
        int key = 2;
        String value = "some value";
        cache.save(key, value);
        assertNotNull("Cache should save its values in memory storage", memoryStorage.get(key));
        assertNotNull("Cache should save its values in disk storage", diskStorage.get(key));
        key = 3;
        value = "some other value";
        cache.save(key, value);
        assertNull("Values should not be saved in the memory storage when its full", memoryStorage.get(key));
        assertNotNull("Cache should save its values in disk when memory storage is full", diskStorage.get(key));
    }

    @Test
    public void testGetWhenMemoryStorageIsFull() {
        int key = 2;
        String value = "some value";
        cache.save(key, value);
        key = 3;
        value = "some other value";
        cache.save(key, value);
        assertNull("Values should not be saved in the memory storage when its full", memoryStorage.get(key));
        assertEquals("Cache should return a value stored in disk memory even when the heap memory is full",
                cache.get(key), value);
    }

}
