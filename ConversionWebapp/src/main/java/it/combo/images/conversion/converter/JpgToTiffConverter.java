package it.combo.images.conversion.converter;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class JpgToTiffConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("jpg") && targetExtension.equalsIgnoreCase("tiff");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        // Specificare "tiff" come formato target. Assicurarsi di avere il supporto TIFF (ad es. tramite JAI ImageIO)
        boolean result = ImageIO.write(image, "tiff", outputFile);
        if (!result) {
            throw new IOException("Conversione in TIFF fallita");
        }
        return outputFile;
    }
}
