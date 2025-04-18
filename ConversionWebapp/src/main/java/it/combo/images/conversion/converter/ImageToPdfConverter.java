package it.combo.images.conversion.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageToPdfConverter implements FileConverter {

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return targetExtension.equalsIgnoreCase("pdf");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Impossibile leggere l'immagine: " + inputFile.getAbsolutePath());
        }
        PDDocument document = new PDDocument();
        PDRectangle pageSize = new PDRectangle(image.getWidth(), image.getHeight());
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
        contentStream.close();
        document.save(outputFile);
        document.close();
        return outputFile;
    }
}
