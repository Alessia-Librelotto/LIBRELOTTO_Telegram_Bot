package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Dato l'hash di un file stabilisce se il file è sicuro o se non è mai stato analizzato
public class FileCheckService {

    public static FileCheckResult check(String hash) throws Exception {

        URL url = new URL("https://www.virustotal.com/api/v3/files/" + hash);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        if (con.getResponseCode() == 404) {
            // File sconosciuto: codice 404
            return new FileCheckResult(404,
                    "❓ File sconosciuto\nQuesto file non è presente nel database VirusTotal.\n" +
                            "Si consiglia di non eseguirlo.");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONObject json = new JSONObject(sb.toString());

        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        int malicious = stats.getInt("malicious");
        int suspicious = stats.getInt("suspicious");

        // Determina codice e messaggio
        int statusCode;
        String message;

        if (suspicious > 0) {
            statusCode = 300; // File sospetto
            message = "⚠️ File sospetto.\nAlcuni motori antivirus lo considerano rischioso. Si consiglia di non eseguirlo!";
        } else {
            statusCode = 200; // File sicuro
            message = "✅ File sicuro.\nNessun motore antivirus lo segnala.";
        }

        return new FileCheckResult(statusCode, message);
    }

    // Classe interna per contenere il risultato
    public static class FileCheckResult {
        private final int statusCode;
        private final String message;

        public FileCheckResult(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }
}
