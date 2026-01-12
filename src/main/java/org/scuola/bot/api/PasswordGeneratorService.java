package org.scuola.bot.api;

import java.security.SecureRandom;

// Servizio che genera password sicure in modo casuale
// Utilizza SecureRandom per garantire maggiore sicurezza
public class PasswordGeneratorService {

    // Insieme di lettere minuscole
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    // Insieme di lettere maiuscole
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Insieme di cifre numeriche
    private static final String DIGITS = "0123456789";

    // Insieme di simboli speciali
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.<>?";

    // Unione di tutti i caratteri disponibili
    // Verrà usata per riempire la password in modo casuale
    private static final String ALL = LOWER + UPPER + DIGITS + SYMBOLS;

    // Generatore di numeri casuali crittograficamente sicuro
    private static final SecureRandom random = new SecureRandom();

    // Metodo principale che genera una password
    // length = lunghezza desiderata della password
    public static String generate(int length) {

        // Controllo di sicurezza: lunghezza minima consigliata
        if (length < 8) {
            throw new IllegalArgumentException(
                    "La password deve avere almeno 8 caratteri"
            );
        }

        // StringBuilder per costruire la password
        StringBuilder sb = new StringBuilder();

        // Garantisce almeno un carattere per ogni categoria
        // (minuscola, maiuscola, numero, simbolo)
        sb.append(randomChar(LOWER));
        sb.append(randomChar(UPPER));
        sb.append(randomChar(DIGITS));
        sb.append(randomChar(SYMBOLS));

        // Riempie il resto della password con caratteri casuali
        // scelti da tutte le categorie disponibili
        for (int i = 4; i < length; i++) {
            sb.append(randomChar(ALL));
        }

        // Mischia i caratteri per evitare che i primi 4
        // siano sempre nello stesso ordine
        return shuffle(sb.toString());
    }

    // Restituisce un carattere casuale
    // preso dalla stringa sorgente passata
    private static char randomChar(String source) {
        return source.charAt(
                random.nextInt(source.length())
        );
    }

    // Metodo che mescola i caratteri della password
    // utilizzando un algoritmo
    private static String shuffle(String input) {

        // Converte la stringa in un array di caratteri
        char[] chars = input.toCharArray();

        // Ciclo di mescolamento
        for (int i = chars.length - 1; i > 0; i--) {

            // Indice casuale
            int j = random.nextInt(i + 1);

            // Scambio dei caratteri
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        // Ritorna la password finale come stringa
        return new String(chars);
    }
}