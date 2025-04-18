package it.combo.images.conversion.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.combo.images.conversion.config.StorageProperties;
import it.combo.images.conversion.converter.FileConverter;
import it.combo.images.conversion.converter.factory.ConverterFactory;

@Service
public class ConversionService {

    private final ConverterFactory converterFactory;
    private final StorageProperties storageProperties;

    @Autowired
    public ConversionService(ConverterFactory converterFactory, StorageProperties storageProperties) {
        this.converterFactory = converterFactory;
        this.storageProperties = storageProperties;
    }

//    public File convertFile(File inputFile, String targetFormat) throws IOException {
//        String fileName = inputFile.getName();
//        int dotIndex = fileName.lastIndexOf('.');
//        if (dotIndex < 0) {
//            throw new IOException("File senza estensione: " + fileName);
//        }
//        
//        String inputExtension = fileName.substring(dotIndex + 1).toLowerCase();
//        // Ottieni il converter adatto
//        FileConverter converter = converterFactory.getConverter(inputExtension, targetFormat);
//        
//        // Genera un UUID per creare una cartella univoca per questo upload (o per questo utente)
//        String uuid = UUID.randomUUID().toString();
//        File userDir = new File(storageProperties.getDownloadDir(), uuid);
//        if (!userDir.exists()) {
//            userDir.mkdirs();
//        }
//        
//        // Estrae il nome base
//        String baseName = fileName.substring(0, dotIndex);
//        // Se il nome base termina già con "_converted", lo rimuoviamo
//        if (baseName.endsWith("_converted")) {
//            baseName = baseName.substring(0, baseName.length() - "_converted".length());
//        }
//        // Genera il nome dell'output con il suffisso _converted una sola volta
//        String outputFileName = baseName + "_converted." + targetFormat;
//        
//        // Usa la directory configurata per salvare il file convertito
//        File downloadDir = new File(storageProperties.getDownloadDir());
//        if (!downloadDir.exists()) {
//            downloadDir.mkdirs();
//        }
//        File outputFile = new File(downloadDir, outputFileName);
//        
//        // Ottieni il converter adatto dalla factory
//        String inputExtension = fileName.substring(dotIndex + 1).toLowerCase();
//        FileConverter converter = converterFactory.getConverter(inputExtension, targetFormat);
//        
//        return converter.convert(inputFile, outputFile);
//    }
    
    public File convertFile(File inputFile, String targetFormat) throws IOException {
        String fileName = inputFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IOException("File senza estensione: " + fileName);
        }
        String inputExtension = fileName.substring(dotIndex + 1).toLowerCase();
        FileConverter converter = converterFactory.getConverter(inputExtension, targetFormat);
        
        // Genera un UUID per creare una cartella univoca per questo upload
        String uuid = UUID.randomUUID().toString();
        File userDir = new File(storageProperties.getDownloadDir(), uuid);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        
        // Costruisci il nome del file di output
        String baseName = fileName.substring(0, dotIndex);
        if (baseName.endsWith("_converted")) {
            baseName = baseName.substring(0, baseName.length() - "_converted".length());
        }
        String outputFileName = baseName + "_converted." + targetFormat;
        File outputFile = new File(userDir, outputFileName);
        
        return converter.convert(inputFile, outputFile);
    }
}
