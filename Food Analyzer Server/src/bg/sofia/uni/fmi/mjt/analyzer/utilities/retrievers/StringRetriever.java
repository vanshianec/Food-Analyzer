package bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers;

/**
 * String key retrieving implementation based on the {@code KeyRetriever} interface
 */

public class StringRetriever implements KeyRetriever<String> {

    /**
     * Identity type function which returns the passed {@code key} parameter without any modifications
     *
     * @param key The key based on which the object will be created
     * @return The same representation as the {@code key} param
     */

    @Override
    public String getValueFromString(String key) {
        return key;
    }
}
