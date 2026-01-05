package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Dato un IP valido stabilisce se è sicuro o dannoso
// Il controllo viene effettuato tramite l'API VirusTotal
public class CheckIpService {

    public static CheckIpResult check(String ip) throws Exception {

        // Endpoint VirusTotal per IP
        // L'IP viene aggiunto direttamente all'URL come richiesto dall'API
        URL url = new URL("https://www.virustotal.com/api/v3/ip_addresses/" + ip);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Imposta il metodo HTTP GET
        con.setRequestMethod("GET");

        // Imposta la chiave API nell'header (richiesta da VirusTotal)
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

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

        // Estrazione delle statistiche finali dell'analisi
        // Contiene il numero di motori che segnalano l'IP come malevolo
        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        // Estrazione dei risultati dettagliati di ogni motore antivirus
        JSONObject results = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_results");

        // Numero di motori che considerano l'IP dannoso
        int malicious = stats.getInt("malicious");

        // Costruzione dei dettagli mostrando solo i motori che segnalano problemi
        StringBuilder details = new StringBuilder();
        for (String engine : results.keySet()) {
            JSONObject engineData = results.getJSONObject(engine);
            String category = engineData.getString("category");
            String result = engineData.optString("result", "unknown");

            // Vengono mostrati solo i motori che classificano l'IP come malevolo
            if (category.equals("malicious")) {
                details.append("- ")
                        .append(engine)
                        .append(": ")
                        .append(result)
                        .append("\n");
            }
        }

        // Determina codice di stato HTTP simulato e messaggio per l'utente
        int statusCode;
        String message;

        // Se almeno un motore segnala l'IP come malevolo
        if (malicious > 0) {
            statusCode = 451; // IP pericoloso
            message = "❌ IP pericoloso!\n" +
                    "Motivi: " + malicious + " motori antivirus lo segnalano.\n" +
                    "Dettagli:\n" + details;
        } else {
            // Nessun motore segnala problemi
            statusCode = 200; // IP sicuro
            message = "✅ IP sicuro.\nNessun motore antivirus lo segnala.";
        }

        // Ritorna il risultato incapsulato in un oggetto
        return new CheckIpResult(statusCode, message);
    }

    // Classe interna per contenere il risultato
    // Serve a separare la logica di analisi dalla presentazione nel bot
    public static class CheckIpResult {
        private final int statusCode;
        private final String message;

        public CheckIpResult(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        // Ritorna il codice di stato da usare anche per l'immagine http.dog
        public int getStatusCode() {
            return statusCode;
        }

        // Ritorna il messaggio testuale da inviare all'utente
        public String getMessage() {
            return message;
        }
    }
}
