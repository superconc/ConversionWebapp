package it.combo.images.conversion.converter;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageFormatConverter implements FileConverter {

    private final String targetFormat;

    // Possiamo usare un costruttore personalizzato oppure creare una factory di istanze.
    // Qui, se necessario, ogni istanza viene creata con il formato di destinazione.
    public ImageFormatConverter() {
        this.targetFormat = null; // L'implementazione predefinita richiede che venga creato l'istanza con il formato desiderato via factory.
    }
    
    public ImageFormatConverter(String targetFormat) {
        this.targetFormat = targetFormat;
    }
    
    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        // Supporta la conversione se il target richiesto è uguale a quello passato al costruttore
        // e non è coperta dagli altri converter (per esempio, se input è già uguale al target lo escludiamo)
        return !inputExtension.equalsIgnoreCase(targetExtension)
                && targetExtension.equalsIgnoreCase(targetFormat);
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        boolean result = ImageIO.write(image, targetFormat, outputFile);
        if (!result) {
            throw new IOException("Conversione in " + targetFormat + " fallita");
        }
        return outputFile;
    }
}
