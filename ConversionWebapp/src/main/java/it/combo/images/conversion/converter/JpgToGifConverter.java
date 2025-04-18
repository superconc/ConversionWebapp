package it.combo.images.conversion.converter;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class JpgToGifConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("jpg") && targetExtension.equalsIgnoreCase("gif");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        boolean result = ImageIO.write(image, "gif", outputFile);
        if (!result) {
            throw new IOException("Conversione in GIF fallita");
        }
        return outputFile;
    }
}
