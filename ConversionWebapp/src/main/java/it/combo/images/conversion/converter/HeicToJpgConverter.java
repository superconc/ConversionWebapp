package it.combo.images.conversion.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeicToJpgConverter implements FileConverter {

    private final String heifConverterPath;
    
    // Inietta dalla proprietà "heif-converter.path"
    public HeicToJpgConverter(@Value("${heif-converter.path}") String heifConverterPath) {
        this.heifConverterPath = heifConverterPath;
    }

    @Override
    public boolean supports(String inputExtension, String targetExtension) {
        return inputExtension.equalsIgnoreCase("heic") && targetExtension.equalsIgnoreCase("jpg");
    }

    @Override
    public File convert(File inputFile, File outputFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                heifConverterPath,
                inputFile.getAbsolutePath(),
                outputFile.getAbsolutePath());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("heif-convert fallito con codice di uscita: " + exitCode);
            }
        } catch (InterruptedException e) {
            throw new IOException("Processo interrotto", e);
        }
        if (!outputFile.exists()) {
            throw new IOException("Il file di output non è stato creato: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }
}
