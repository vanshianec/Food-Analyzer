package bg.sofia.uni.fmi.mjt.analyzer.utilities.retrievers;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntRetrieveTest {

    private static KeyRetriever<Integer> intRetriever;

    @BeforeClass
    public static void initializeRetriever() {
        intRetriever = new IntRetriever();
    }

    @Test
    public void testGetValueFromStringWithNegativeInteger() {
        Integer actual = intRetriever.getValueFromString("-394");
        Integer expected = -394;
        assertEquals("Int retriever should return a negative int value as the provided string representation",
                expected, actual);
    }

    @Test
    public void testGetValueFromStringWithPositiveInteger() {
        Integer actual = intRetriever.getValueFromString("394");
        Integer expected = 394;
        assertEquals("Int retriever should return a positive int value as the provided string representation",
                expected, actual);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetValueFromStringWithInvalidInteger() {
        intRetriever.getValueFromString("34.54gd");
    }
}
