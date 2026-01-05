package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Dato l'hash di un file stabilisce se il file è sicuro o se non è mai stato analizzato
// Il controllo viene effettuato interrogando l'API VirusTotal
public class FileCheckService {

    public static FileCheckResult check(String hash) throws Exception {

        // Endpoint VirusTotal per il controllo dei file tramite hash
        URL url = new URL("https://www.virustotal.com/api/v3/files/" + hash);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Imposta il metodo HTTP GET
        con.setRequestMethod("GET");

        // Inserisce la API key nell'header della richiesta
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        // Se VirusTotal restituisce 404 significa che il file non è presente nel database
        // quindi non è mai stato analizzato
        if (con.getResponseCode() == 404) {
            // File sconosciuto: codice 404
            return new FileCheckResult(
                    404,
                    "❓ File sconosciuto\n" +
                            "Questo file non è presente nel database VirusTotal.\n" +
                            "Si consiglia di non eseguirlo."
            );
        }

        // Lettura della risposta JSON restituita dall'API
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // Parsing della risposta JSON completa
        JSONObject json = new JSONObject(sb.toString());

        // Estrazione delle statistiche dell'ultima analisi
        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        // Numero di motori antivirus che segnalano il file come malevolo
        int malicious = stats.getInt("malicious");

        // Numero di motori antivirus che segnalano il file come sospetto
        int suspicious = stats.getInt("suspicious");

        // Determina codice di stato HTTP simulato e messaggio per l'utente
        int statusCode;
        String message;

        // Se almeno un motore segnala il file come sospetto
        if (suspicious > 0) {
            statusCode = 300; // File sospetto
            message = "⚠️ File sospetto.\n" +
                    "Alcuni motori antivirus lo considerano rischioso. " +
                    "Si consiglia di non eseguirlo!";
        } else {
            // Nessun motore segnala problemi
            statusCode = 200; // File sicuro
            message = "✅ File sicuro.\nNessun motore antivirus lo segnala.";
        }

        // Ritorna il risultato incapsulato in un oggetto
        return new FileCheckResult(statusCode, message);
    }

    // Classe interna per contenere il risultato
    // Serve a separare la logica di analisi dalla presentazione nel bot
    public static class FileCheckResult {
        private final int statusCode;
        private final String message;

        public FileCheckResult(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        // Ritorna il codice di stato, usato anche per associare un'immagine
        public int getStatusCode() {
            return statusCode;
        }

        // Ritorna il messaggio testuale da inviare all'utente Telegram
        public String getMessage() {
            return message;
        }
    }
}
