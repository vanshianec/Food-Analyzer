package bg.sofia.uni.fmi.mjt.analyzer.command;

import bg.sofia.uni.fmi.mjt.analyzer.api.FoodService;
import bg.sofia.uni.fmi.mjt.analyzer.storage.Storage;


/**
 * An abstract class which uses a food service and a storage from its parent class
 * to save and retrieve data which will be used to execute a command
 *
 * @param <K> The key object by which data will be stored and retrieved
 * @param <V> The value object which will be stored and retrieved
 */

public abstract class AbstractServiceCommand<K, V> extends AbstractCommand<K, V> {

    protected FoodService foodService;

    protected AbstractServiceCommand(Storage<K, V> storage, FoodService foodService) {
        super(storage);
        this.foodService = foodService;
    }
}
