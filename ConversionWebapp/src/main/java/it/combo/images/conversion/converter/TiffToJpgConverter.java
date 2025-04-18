package it.combo.images.conversion.converter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class TiffToJpgConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("tiff") && targetExtension.equalsIgnoreCase("jpg");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        // Legge l'immagine TIFF
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere il file TIFF: " + inputFile.getAbsolutePath());
        }
        // Crea una nuova immagine in formato RGB per rimuovere eventuale trasparenza
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        // Imposta lo sfondo bianco (opzionale, a seconda delle esigenze)
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        // Disegna l'immagine TIFF sopra lo sfondo bianco
        g.drawImage(image, 0, 0, null);
        g.dispose();
        // Scrive l'immagine in formato JPG
        boolean result = ImageIO.write(rgbImage, "jpg", outputFile);
        if (!result) {
            throw new IOException("Conversione in JPG fallita");
        }
        return outputFile;
    }
}
