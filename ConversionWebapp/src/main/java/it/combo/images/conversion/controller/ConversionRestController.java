package it.combo.images.conversion.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.combo.images.conversion.config.StorageProperties;
import it.combo.images.conversion.service.ConversionService;

@RestController
public class ConversionRestController {

	@Autowired
	private StorageProperties storageProperties;

	@Autowired
	private ConversionService conversionService;
	
	@PostMapping("/uploadAjax")
	public ResponseEntity<Map<String, Object>> handleUploadAjax(
	        @RequestParam("files") MultipartFile[] files,
	        @RequestParam("conversionType") String conversionType) {
	    Map<String, Object> response = new HashMap<>();

	    if (files == null || files.length == 0) {
	        response.put("message", "Nessun file selezionato.");
	        return ResponseEntity.badRequest().body(response);
	    }

	    // Verifica che tutti i file siano dello stesso formato
	    String firstExt = getExtension(files[0].getOriginalFilename());
	    for (MultipartFile f : files) {
	        if (!getExtension(f.getOriginalFilename()).equalsIgnoreCase(firstExt)) {
	            response.put("message", "Tutti i file devono essere dello stesso formato!");
	            return ResponseEntity.badRequest().body(response);
	        }
	    }

	    List<String> downloadLinks = new ArrayList<>();
	    List<String> fileNames = new ArrayList<>();
	    try {
	        File storageDir = new File(storageProperties.getDownloadDir());
	        if (!storageDir.exists()) {
	            storageDir.mkdirs();
	        }
	        for (MultipartFile file : files) {
	            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
	            File uploadedFile = new File(storageDir, originalFileName);
	            Files.copy(file.getInputStream(), uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	            File convertedFile = conversionService.convertFile(uploadedFile, conversionType);
	            // Calcola il percorso relativo per il download (includendo la sottocartella)
	            Path relativePathObj = storageDir.toPath().relativize(convertedFile.toPath());
	            String relativePath = relativePathObj.toString().replace("\\", "/");
	            downloadLinks.add("/download?file=" + relativePath);
	            // Per la visualizzazione, prendiamo solo il nome base del file convertito
	            fileNames.add(convertedFile.getName());
	        }
	        response.put("message", "Conversione completata per " + files.length + " file.");
	        response.put("downloadLinks", downloadLinks);
	        response.put("fileNames", fileNames);
	        return ResponseEntity.ok(response);
	    } catch (IOException e) {
	        e.printStackTrace();
	        response.put("message", "Errore durante la conversione: " + e.getMessage());
	        return ResponseEntity.status(500).body(response);
	    }
	}

	private String getExtension(String filename) {
	    if (filename == null) return "";
	    int dotIndex = filename.lastIndexOf('.');
	    return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
	}
}
