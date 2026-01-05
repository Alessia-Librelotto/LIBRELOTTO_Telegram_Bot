package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

// Dato un URL stabilisce se è sicuro o se è pericoloso
// Utilizza l'API VirusTotal per analizzare la reputazione del sito
public class UrlScanService {

    public static UrlScanResult scan(String site) throws Exception {

        // Step 1: codifica l'URL in Base64 (URL-safe)
        // VirusTotal richiede questa codifica per identificare univocamente l'URL
        String encodedUrl = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(site.getBytes());

        // Step 2: crea la connessione GET per recuperare il report dell'URL
        URL url = new URL("https://www.virustotal.com/api/v3/urls/" + encodedUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Imposta il metodo HTTP
        con.setRequestMethod("GET");

        // Inserisce la API key nell'header della richiesta
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        // Lettura della risposta JSON dall'API
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // Parsing del JSON completo restituito da VirusTotal
        JSONObject json = new JSONObject(sb.toString());

        // Estrazione delle statistiche dell'ultima analisi
        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        // Estrazione dei risultati dettagliati per ogni motore antivirus
        JSONObject results = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_results");

        // Numero di motori che segnalano l'URL come malevolo
        int malicious = stats.getInt("malicious");

        // Costruzione dei dettagli solo per i motori che segnalano malware
        StringBuilder details = new StringBuilder();
        for (String engine : results.keySet()) {
            JSONObject engineData = results.getJSONObject(engine);
            String category = engineData.getString("category");
            String result = engineData.optString("result", "unknown");

            // Considera solo i risultati classificati come "malicious"
            if (category.equals("malicious")) {
                details.append("- ")
                        .append(engine)
                        .append(": ")
                        .append(result)
                        .append("\n");
            }
        }

        // Determina codice di stato HTTP simulato e messaggio finale
        int statusCode;
        String message;

        // Se almeno un motore segnala l'URL come malevolo
        if (malicious > 0) {
            statusCode = 451; // URL pericoloso
            message = "❌ URL pericoloso!\n" +
                    "Motivi: " + malicious + " motori antivirus segnalano malware.\n" +
                    "Dettagli:\n" + details;
        } else {
            // Nessuna segnalazione da parte dei motori antivirus
            statusCode = 200; // URL sicuro
            message = "✅ URL sicuro.\nNessun motore antivirus lo segnala.";
        }

        // Ritorna il risultato incapsulato
        return new UrlScanResult(statusCode, message);
    }

    // Classe interna per contenere il risultato
    // Serve a separare la logica di analisi dalla risposta del bot
    public static class UrlScanResult {
        private final int statusCode;
        private final String message;

        public UrlScanResult(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        // Codice di stato utilizzato anche per mostrare l'immagine associata
        public int getStatusCode() {
            return statusCode;
        }

        // Messaggio testuale da inviare all'utente Telegram
        public String getMessage() {
            return message;
        }
    }
}
