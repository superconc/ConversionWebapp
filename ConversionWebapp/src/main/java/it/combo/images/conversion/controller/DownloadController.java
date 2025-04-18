package it.combo.images.conversion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.combo.images.conversion.config.StorageProperties;

@RestController
public class DownloadController {

	@Autowired
	private StorageProperties storageProperties;

	@GetMapping("/download")
	public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("file") String fileRelativePath) {
	    try {
	        System.out.println("Download file: " + fileRelativePath);
	        File allowedDir = new File(storageProperties.getDownloadDir());
	        // Combina la directory di download col percorso relativo, che ora include la sottocartella
	        File file = new File(allowedDir, fileRelativePath);
	        if (!file.exists() || !file.isFile()) {
	            System.err.println("File non trovato: " + file.getAbsolutePath());
	            return ResponseEntity.notFound().build();
	        }
	        String canonicalAllowed = allowedDir.getCanonicalPath();
	        String canonicalFile = file.getCanonicalPath();
	        if (!canonicalFile.startsWith(canonicalAllowed)) {
	            System.err.println("Tentativo di accesso a file fuori dalla directory consentita.");
	            return ResponseEntity.notFound().build();
	        }
	        FileSystemResource resource = new FileSystemResource(file);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentLength(file.length())
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }
	}
	
	@GetMapping("/downloadAll")
	public ResponseEntity<InputStreamResource> downloadAll(@RequestParam("files") String filesParam) {
	    // Si assume che filesParam contenga i nomi dei file separati da virgola
	    String[] fileNamesParam = filesParam.split(",");
	    // Crea un file ZIP temporaneo
	    Path zipPath;
	    try {
	        zipPath = Files.createTempFile("converted_", ".zip");
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }

	    File storageDir = new File(storageProperties.getDownloadDir());
	    // Comprime i file nello ZIP
	    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
	        for (String fileNameParam : fileNamesParam) {
	            fileNameParam = fileNameParam.trim(); // Rimuove spazi eventuali
	            if (fileNameParam.isEmpty()) continue;
	            // Cerca il file in modo ricorsivo nella directory storageDir
	            File file = findFileByName(storageDir, fileNameParam);
	            if (file == null || !file.isFile()) {
	                System.err.println("File non trovato: " + fileNameParam);
	                continue;
	            }
	            System.out.println("Aggiungo al zip: " + file.getAbsolutePath());
	            // Aggiunge una entry nello zip usando solo il nome del file (senza cartelle)
	            ZipEntry zipEntry = new ZipEntry(file.getName());
	            zos.putNextEntry(zipEntry);
	            Files.copy(file.toPath(), zos);
	            zos.closeEntry();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }

	    File zipFile = zipPath.toFile();
	    InputStreamResource resource;
	    try {
	        resource = new InputStreamResource(new FileInputStream(zipFile));
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Disposition", "attachment; filename=converted_files.zip");
	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(zipFile.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}

	// Metodo ricorsivo per cercare un file per nome all'interno di una directory e delle sue sottocartelle
	private File findFileByName(File directory, String fileName) {
	    File[] files = directory.listFiles();
	    if (files != null) {
	        for (File file : files) {
	            if (file.isDirectory()) {
	                File found = findFileByName(file, fileName);
	                if (found != null) {
	                    return found;
	                }
	            } else if (file.getName().equals(fileName)) {
	                return file;
	            }
	        }
	    }
	    return null;
	}


}
