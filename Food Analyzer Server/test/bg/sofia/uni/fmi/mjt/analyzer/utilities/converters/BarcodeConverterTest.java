package bg.sofia.uni.fmi.mjt.analyzer.utilities.converters;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.BarcodeReadingException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.ImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidPathException;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class BarcodeConverterTest {

    private static final String BARCODE_IMAGES_PATH = "test" + File.separator + "resources" + File.separator + "barcode-images";

    @Test(expected = InvalidPathException.class)
    public void testGetGtinUpcFromImageWithNullArgument() throws BarcodeReadingException, ImageNotFoundException, InvalidPathException {
        BarcodeConverter.getGtinUpcFromImage(null);
    }

    @Test(expected = InvalidPathException.class)
    public void testGetGtinUpcFromImageWithEmptyArgument() throws BarcodeReadingException, ImageNotFoundException, InvalidPathException {
        BarcodeConverter.getGtinUpcFromImage("");
    }

    @Test
    public void testGetGtinUpcFromImageWithValidImage() throws BarcodeReadingException, ImageNotFoundException, InvalidPathException {
        String validImage = BARCODE_IMAGES_PATH + File.separator + "validImage.gif";
        String actualCode = BarcodeConverter.getGtinUpcFromImage(validImage);
        String expectedCode = "025484007109";
        assertEquals("Barcodes should match when a valid image is provided", expectedCode, actualCode);
    }

    @Test(expected = ImageNotFoundException.class)
    public void testGetGtinUpcFromImageWithInvalidImage() throws BarcodeReadingException, ImageNotFoundException, InvalidPathException {
        String invalidImage = BARCODE_IMAGES_PATH + File.separator + "invalidImage.png";
        BarcodeConverter.getGtinUpcFromImage(invalidImage);
    }

    @Test(expected = BarcodeReadingException.class)
    public void testGetGtinUpcFromImageWithInvalidPath() throws BarcodeReadingException, ImageNotFoundException, InvalidPathException {
        String invalidImage = BARCODE_IMAGES_PATH + File.separator + "someInvalidPath.png";
        BarcodeConverter.getGtinUpcFromImage(invalidImage);
    }
}
