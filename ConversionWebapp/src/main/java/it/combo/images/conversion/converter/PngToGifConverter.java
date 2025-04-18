package it.combo.images.conversion.converter;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class PngToGifConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("png") && targetExtension.equalsIgnoreCase("gif");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine PNG dal file di input
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        // Scrive l'immagine nel formato GIF nel file di output
        boolean result = ImageIO.write(image, "gif", outputFile);
        if (!result) {
            throw new IOException("Conversione in GIF fallita");
        }
        return outputFile;
    }
}
