package it.combo.images.conversion.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class TiffToGifConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("tiff") && targetExtension.equalsIgnoreCase("gif");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine TIFF
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere il file TIFF: " + inputFile.getAbsolutePath());
        }
        // Scrive l'immagine in formato GIF
        boolean result = ImageIO.write(image, "gif", outputFile);
        if (!result) {
            throw new IOException("Conversione in GIF fallita");
        }
        return outputFile;
    }
}
