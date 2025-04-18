package it.combo.images.conversion.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class GifToPngConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("gif") && targetExtension.equalsIgnoreCase("png");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine GIF
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine GIF: " + inputFile.getAbsolutePath());
        }
        // Scrive l'immagine in formato PNG
        boolean result = ImageIO.write(image, "png", outputFile);
        if (!result) {
            throw new IOException("Conversione in PNG fallita");
        }
        return outputFile;
    }
}
