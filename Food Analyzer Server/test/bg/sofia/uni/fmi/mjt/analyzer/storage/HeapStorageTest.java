package bg.sofia.uni.fmi.mjt.analyzer.storage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HeapStorageTest {

    private BoundedStorage<Integer, String> storageWithOneElementCapacity;

    @Before
    public void initializeStorage() {
        storageWithOneElementCapacity = new HeapStorage<>(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStorageInitializationWithNegativeCapacity() {
        new HeapStorage<Integer, String>(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStorageInitializationWithZeroCapacity() {
        new HeapStorage<Integer, String>(0);
    }

    @Test
    public void testGetShouldReturnNullWhenAValueIsMissing() {
        int key = 1;
        assertNull("Empty storage should not return an element", storageWithOneElementCapacity.get(key));
        storageWithOneElementCapacity.save(key, "some value");
        int missingKey = 3;
        assertNull("Non empty storage should not return a value by a missing key", storageWithOneElementCapacity.get(missingKey));
    }

    @Test
    public void testGetShouldReturnAnExistingElement() {
        int key = 1;
        String value = "some value";
        storageWithOneElementCapacity.save(key, value);
        String returnValue = storageWithOneElementCapacity.get(key);
        assertEquals("Storage should return the value corresponding to its key", value, returnValue);
    }

    @Test
    public void testSaveShouldSaveAnElementInTheStorage() {
        int key = 1;
        String value = "some value";
        storageWithOneElementCapacity.save(key, value);
        String returnValue = storageWithOneElementCapacity.get(key);
        assertEquals("Storage should be contain the values saved in it", value, returnValue);
        assertTrue("Storage be full when the inserted values reach its capacity", storageWithOneElementCapacity.isFull());
    }

    @Test
    public void testSaveShouldNotSaveElementWhenTheCapacityIsReached() {
        int storedKey = 1;
        int notStoredKey = 2;
        String storedValue = "some value";
        String notStoredValue = "not saved";

        storageWithOneElementCapacity.save(storedKey, storedValue);
        assertEquals("Storage should save a element when the capacity is not reached",
                storageWithOneElementCapacity.get(storedKey), storedValue);
        /* this should not be saved since the capacity is 1 */
        storageWithOneElementCapacity.save(notStoredKey, notStoredValue);
        assertEquals("Previously stored value should not be removed when the capacity is reached",
                storageWithOneElementCapacity.get(storedKey), storedValue);
        assertNull("Storage should not save more values when the capacity is reached",
                storageWithOneElementCapacity.get(notStoredKey));
    }

    @Test
    public void testIsFullWhenTheStorageIsNotFull() {
        assertFalse("Newly created valid bounded storage should not be full",
                storageWithOneElementCapacity.isFull());
    }

    @Test
    public void testIsFullWhenInsertedElementsAreEqualToTheCapacity() {
        storageWithOneElementCapacity.save(1, "some value");
        assertTrue("Storage should be full when the added elements match the capacity",
                storageWithOneElementCapacity.isFull());
    }
}
