package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

//Dato un URL stabilisce se è sicuro o se è pericoloso
public class UrlScanService {

    // Metodo principale per controllare un URL
    public static UrlScanResult scan(String site) throws Exception {

        // Step 1: codifica l'URL in Base64 come richiesto dall'API
        String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(site.getBytes());

        // Step 2: crea la connessione GET per recuperare il report
        URL url = new URL("https://www.virustotal.com/api/v3/urls/" + encodedUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONObject json = new JSONObject(sb.toString());

        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        JSONObject results = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_results");

        int malicious = stats.getInt("malicious");
        int suspicious = stats.getInt("suspicious");

        // Costruzione dettagli
        StringBuilder details = new StringBuilder();
        for (String engine : results.keySet()) {
            JSONObject engineData = results.getJSONObject(engine);
            String category = engineData.getString("category");
            String result = engineData.optString("result", "unknown");
            if (category.equals("malicious")) {
                details.append("- ").append(engine).append(": ").append(result).append("\n");
            }
        }

        // Determina codice e messaggio
        int statusCode;
        String message;

        if (malicious > 0) {
            statusCode = 451; // URL pericoloso
            message = "❌ URL pericoloso!\nMotivi: " + malicious + " motori antivirus segnalano malware.\nDettagli:\n" + details;
        } else {
            statusCode = 200; // URL sicuro
            message = "✅ URL sicuro.\nNessun motore antivirus lo segnala.";
        }

        return new UrlScanResult(statusCode, message);
    }

    // Classe interna per contenere il risultato
    public static class UrlScanResult {
        private final int statusCode;
        private final String message;

        public UrlScanResult(int statusCode, String message) {
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
