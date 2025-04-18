package it.combo.images.conversion.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.combo.images.conversion.service.ConversionService;

@Controller
public class ConversionController {

    // Definisce la cartella temporanea per i file di conversione
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "conversioneFiles";

    @Autowired
    private ConversionService conversionService;

    /**
     * Mostra la pagina di upload (template "upload.html").
     */
    @GetMapping("/")
    public String index(Model model) {
        return "conversion";
    }

    /**
     * Gestisce l'upload del file, esegue la conversione e prepara il link per il download.
     * Il file viene salvato nella cartella temporanea TEMP_DIR.
     *
     * @param file           il file caricato dall'utente
     * @param conversionType il formato di destinazione (es. "pdf", "jpg", ecc.)
     * @param model          il model per passare gli attributi alla view
     * @return il template "upload" da rendere
     */
    @PostMapping("/conversion")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               @RequestParam("conversionType") String conversionType,
                               Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Nessun file selezionato.");
            return "upload";
        }

        try {
            // Crea la cartella temporanea se non esiste
            Path tempDirPath = Paths.get(TEMP_DIR);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }

            // Salva il file caricato nella cartella temporanea
            File uploadedFile = new File(tempDirPath.toFile(), file.getOriginalFilename());
            Files.copy(file.getInputStream(), uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Effettua la conversione usando il ConversionService
            // Il file convertito viene salvato nella stessa cartella
            File convertedFile = conversionService.convertFile(uploadedFile, conversionType);

            // Prepara l'attributo per il link di download (es: /download?file=nomeFile)
            String downloadLink = "/download?file=" + convertedFile.getName();
            model.addAttribute("message", "Conversione completata: " + convertedFile.getName());
            model.addAttribute("downloadLink", downloadLink);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Errore durante la conversione: " + e.getMessage());
        }
        return "conversion";
    }

    /**
     * Endpoint per il download del file convertito.
     * Riceve il nome del file come parametro e lo recupera dalla cartella temporanea.
     *
     * @param fileName il nome del file convertito
     * @return il file come allegato per il download
     */
//    @GetMapping("/download")
//    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("file") String fileName) {
//        File file = new File(TEMP_DIR, fileName);
//        if (!file.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//        FileSystemResource resource = new FileSystemResource(file);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
}
