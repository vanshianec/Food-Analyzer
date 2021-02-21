package bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers;

/**
 * Integer key retrieving implementation based on the {@code KeyRetriever} interface
 */

public class IntRetriever implements KeyRetriever<Integer> {

    /**
     * Converts the passed String key as an integer value
     *
     * @param key The key based on which the object will be created
     * @return Integer representation of the passed {@code key} param
     * @throws NumberFormatException If the passed String key cannot be converted to an integer value
     */

    @Override
    public Integer getValueFromString(String key) {
        return Integer.parseInt(key);
    }
}
