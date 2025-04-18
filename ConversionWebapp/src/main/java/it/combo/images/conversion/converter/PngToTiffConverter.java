package it.combo.images.conversion.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class PngToTiffConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("png") && targetExtension.equalsIgnoreCase("tiff");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine PNG
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        // Scrive l'immagine in formato TIFF
        boolean result = ImageIO.write(image, "tiff", outputFile);
        if (!result) {
            throw new IOException("Conversione in TIFF fallita. Verifica il supporto TIFF in ImageIO.");
        }
        return outputFile;
    }
}
