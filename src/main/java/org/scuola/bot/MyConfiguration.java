package org.scuola.bot;

import java.io.InputStream;
import java.util.Properties;

 // Classe per la gestione della configurazione dell'applicazione
 // Serve per leggere valori sensibili (API key, token del bot, username, ecc.)
 // da un file esterno config.properties, che NON deve essere caricato su GitHub
public class MyConfiguration {

    // Oggetto Properties che conterrà tutte le coppie chiave-valore
    private static Properties props = new Properties();

    static {
        try {
            // Recupera il file config.properties da resources
            InputStream is = MyConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            // Se il file non viene trovato, lancia un'eccezione
            if (is == null) {
                throw new RuntimeException("config.properties non trovato in resources");
            }

            // Carica le proprietà dal file
            props.load(is);

        } catch (Exception e) {
            // Stampa l'errore in caso di problemi di lettura
            e.printStackTrace();
        }
    }

    // Metodo per ottenere un valore dal file di configurazione
    public static String get(String key) {
        return props.getProperty(key);
    }
}
