package it.combo.images.conversion.converter;

import java.io.File;
import java.io.IOException;

/**
 * Interfaccia generica per la conversione dei file.
 */
public interface FileConverter {
	
	/**
     * Restituisce true se questa implementazione supporta la conversione 
     * dal formato di input a quello di destinazione.
     */
    boolean supports(String inputExtension, String targetExtension);

    /**
     * Converte il file di input e salva il risultato in outputFile.
     *
     * @param inputFile  il file di input
     * @param outputFile il file di output
     * @return il file di output (converted file)
     * @throws IOException in caso di errori durante la conversione
     */
    File convert(File inputFile, File outputFile) throws IOException;
}
