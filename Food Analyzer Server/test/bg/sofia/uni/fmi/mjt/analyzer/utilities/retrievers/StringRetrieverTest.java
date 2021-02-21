package bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringRetrieverTest {

    private static KeyRetriever<String> stringRetriever;

    @BeforeClass
    public static void initializeRetriever() {
        stringRetriever = new StringRetriever();
    }

    @Test
    public void testGetValueFromStringWithEmptyString() {
        String result = stringRetriever.getValueFromString("");
        assertTrue("String retriever should return empty string when an empty string is provided",
                result.isEmpty());
    }

    @Test
    public void testGetValueFromStringWithNonEmptyString() {
        String actual = stringRetriever.getValueFromString("some string");
        String expected = "some string";
        assertEquals("String retriever should return the same string as the provided one",
                expected, actual);
    }
}
