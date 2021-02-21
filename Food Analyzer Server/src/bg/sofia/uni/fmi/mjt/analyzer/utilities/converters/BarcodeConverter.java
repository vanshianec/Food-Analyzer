package bg.sofia.uni.fmi.mjt.analyzer.utilities.converters;

import bg.sofia.uni.fmi.mjt.analyzer.exceptions.BarcodeReadingException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.ImageNotFoundException;
import bg.sofia.uni.fmi.mjt.analyzer.exceptions.InvalidPathException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * An object that is used to create a String representation of an UPC barcode from an image
 */

public final class BarcodeConverter {

    private static final String INVALID_BARCODE_MESSAGE = "The provided barcode image is not in the supported UPC format";
    private static final String BARCODE_READING_ERROR = "An error occurred while trying to read the barcode image";
    private static final String INVALID_PATH_MESSAGE = "The provided path should not be empty";

    private BarcodeConverter() {
    }

    /**
     * Gets an UPC barcode as a String from an image
     *
     * @param filePath The path from which the image will be opened
     * @return The UPC barcode from the image as a String
     * @throws BarcodeReadingException If a error occurs while reading from the image
     * @throws ImageNotFoundException  If the provided image is missing or invalid
     */

    public static String getGtinUpcFromImage(String filePath) throws BarcodeReadingException,
            ImageNotFoundException, InvalidPathException {
        if (filePath == null || filePath.isEmpty()) {
            throw new InvalidPathException(INVALID_PATH_MESSAGE);
        }

        try {
            File file = new File(filePath);
            BufferedImage barcodeBufferedImage = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(barcodeBufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            throw new ImageNotFoundException(INVALID_BARCODE_MESSAGE, e);
        } catch (IOException e) {
            throw new BarcodeReadingException(BARCODE_READING_ERROR, e);
        }
    }
}
