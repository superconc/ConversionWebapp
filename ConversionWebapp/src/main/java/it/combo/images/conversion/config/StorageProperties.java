package it.combo.images.conversion.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StorageProperties {

    // Inietta il percorso configurato nel properties
    @Value("${conversion.download.dir}")
    private String downloadDir;
    
    @Value("${heif-converter.path}")
    private String heifConverterPath;
    
    // Variabile che conterrà il percorso canonico o normalizzato.
    private String normalizedDownloadDir;
    
    @PostConstruct
    public void init() throws IOException {
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        normalizedDownloadDir = dir.getCanonicalPath();
        System.out.println("Directory di download normalizzata: " + normalizedDownloadDir);
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

	public String getHeifConverterPath() {
		return heifConverterPath;
	}

	public void setHeifConverterPath(String heifConverterPath) {
		this.heifConverterPath = heifConverterPath;
	}
}
