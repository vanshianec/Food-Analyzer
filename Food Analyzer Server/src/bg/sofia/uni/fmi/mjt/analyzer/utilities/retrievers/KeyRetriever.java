package bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers;

/**
 * An object used to retrieve a generic value based on a String key
 *
 *  @param <T> The type of the object which will be retrieved
 *
 * @see IntRetriever
 * @see StringRetriever
 */

public interface KeyRetriever<T> {

    /**
     * Returns a generic object created from a String key
     *
     * @param key The key based on which the object will be created
     * @return Instance of the object created from the key
     */

    T getValueFromString(String key);
}
