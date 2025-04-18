package it.combo.images.conversion.converter.factory;

import it.combo.images.conversion.converter.FileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConverterFactory {

    private final List<FileConverter> converters;

    @Autowired
    public ConverterFactory(List<FileConverter> converters) {
        // Spring inietterà tutte le implementazioni di FileConverter annotate con @Component
        this.converters = converters;
    }

    /**
     * Restituisce l'implementazione di FileConverter che supporta la conversione dal formato di input a quello di destinazione.
     *
     * @param inputExtension  es. "jpg", "png", "heic", ecc.
     * @param targetExtension es. "png", "tiff", "gif", "pdf", ecc.
     * @return l'istanza di FileConverter adatta
     */
    public FileConverter getConverter(String inputExtension, String targetExtension) {
        for (FileConverter converter : converters) {
            if (converter.supports(inputExtension, targetExtension)) {
                return converter;
            }
        }
        throw new IllegalArgumentException("Nessun converter trovato per " + inputExtension + " -> " + targetExtension);
    }
}
