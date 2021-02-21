package bg.sofia.uni.fmi.mjt.analyzer.storage;

import bg.sofia.uni.fmi.mjt.analyzer.dtos.FoodReport;
import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.IntRetriever;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DiskStorageTest {

    private static UnboundedStorage<Integer, FoodReport> diskStorage;
    private static final Path objectsPath = Path.of("test", "disk-storage-test-objects", "disk-storage");
    private static FoodReport objectToBeStored;

    @BeforeClass
    public static void initializeStorage() throws IOException {
        clearPath(objectsPath);
        objectToBeStored = new FoodReport("some description", "some ingredients", null);
        diskStorage = new DiskStorage<>(objectsPath, new IntRetriever());
    }

    private static void clearPath(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(objectsPath)) {
            walk.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void testGetShouldReturnNullWhenAValueIsMissing() {
        int key = -1;
        assertNull("Empty storage should not return an element", diskStorage.get(key));
        diskStorage.save(key, objectToBeStored);

        int missingKey = 3;
        assertNull("Non empty storage should not return a value by a missing key", diskStorage.get(missingKey));
    }

    @Test
    public void testSaveShouldSaveAnElementInTheStorage() {
        int key = 1;
        diskStorage.save(key, objectToBeStored);
        assertEquals("Storage should contain the values saved in it", objectToBeStored, diskStorage.get(key));
    }
}
