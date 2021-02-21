package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;

/**
 * An abstract class which uses a storage to save and retrieve data
 * which will be used to execute a command
 *
 * @param <K> The key object by which data will be stored and retrieved
 * @param <V> The value object which will be stored and retrieved
 */

public abstract class AbstractCommand<K, V> implements Command {

    protected Storage<K, V> storage;

    protected AbstractCommand(Storage<K, V> storage) {
        this.storage = storage;
    }
}
