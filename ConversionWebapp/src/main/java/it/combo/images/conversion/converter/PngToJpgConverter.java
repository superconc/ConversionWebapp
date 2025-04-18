package it.combo.images.conversion.converter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class PngToJpgConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("png") && targetExtension.equalsIgnoreCase("jpg");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine PNG
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        // Crea una nuova immagine in formato RGB (senza trasparenza)
        BufferedImage jpgImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        // Imposta lo sfondo bianco
        Graphics2D g = jpgImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        // Disegna l'immagine originale sopra lo sfondo
        g.drawImage(image, 0, 0, null);
        g.dispose();
        // Scrive l'immagine in formato JPG
        boolean result = ImageIO.write(jpgImage, "jpg", outputFile);
        if (!result) {
            throw new IOException("Conversione in JPG fallita");
        }
        return outputFile;
    }
}
