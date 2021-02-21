package bg.sofia.uni.fmi.mjt.analyzer.storage;

import bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers.KeyRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * An object used to store values based on a key in a file
 *
 * @param <K> The key object by which the value will be stored
 * @param <V> The value object which will be stored
 */

public class DiskStorage<K, V> implements UnboundedStorage<K, V> {

    private static final Logger LOGGER = Logger.getLogger(DiskStorage.class.getName());
    private static final String FAILED_TO_RETRIEVE_MESSAGE = "A problem occurred while trying to retrieve an object from a file";
    private static final String FAILED_TO_SAVE_MESSAGE = "A problem occurred while trying to save an object to a file";
    private static final String FAILED_TO_LOAD_FILES = "A problem occurred while trying to load files from a directory";

    private Path directoryPath;
    private final KeyRetriever<K> keyRetriever;

    /**
     * Constructs a new {@code DiskStorage} with the given file path and key retriever
     *
     * @param directoryPath The file path in which the values will be stored
     * @param keyRetriever  The retriever which will be used to retrieve the key object
     *                      by which the value is stored in the file based on the file name
     */

    public DiskStorage(Path directoryPath, final KeyRetriever<K> keyRetriever) {
        this.directoryPath = directoryPath;
        this.keyRetriever = keyRetriever;
    }

    @Override
    public void save(K key, V object) {
        Path path = directoryPath.resolve(Path.of(String.valueOf(key)));
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FAILED_TO_SAVE_MESSAGE, e);
        }
    }

    @Override
    public V get(K key) {
        Path path = directoryPath.resolve(Path.of(String.valueOf(key)));
        File file = new File(path.toUri());
        if (!file.exists()) {
            return null;
        }

        V retrievedObject = null;
        try (var objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            retrievedObject = (V) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, FAILED_TO_RETRIEVE_MESSAGE, e);
        }

        return retrievedObject;
    }

    @Override
    public BoundedStorage<K, V> load(int limit) {
        BoundedStorage<K, V> storage = new HeapStorage<>(limit);

        try (Stream<Path> pathStream = Files.walk(directoryPath)) {
            pathStream.filter(Files::isRegularFile)
                    .limit(limit)
                    .forEach(p -> addPairToStorage(storage, p));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FAILED_TO_LOAD_FILES, e);
        }

        return storage;
    }

    private void addPairToStorage(BoundedStorage<K, V> storage, Path p) {
        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(p))) {
            K key = keyRetriever.getValueFromString(p.getFileName().toString());
            V value = (V) objectInputStream.readObject();
            storage.save(key, value);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, FAILED_TO_RETRIEVE_MESSAGE, e);
        }
    }
}
