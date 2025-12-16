package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class UrlScanService {

    // Metodo principale per controllare un URL
    public static String scan(String site) throws Exception {

        // Step 1: codifica l'URL in Base64 come richiesto dall'API
        String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(site.getBytes());

        // Step 2: crea la connessione GET per recuperare il report
        URL url = new URL("https://www.virustotal.com/api/v3/urls/" + encodedUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        // Step 3: leggi la risposta
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // Step 4: parsifica JSON
        JSONObject json = new JSONObject(sb.toString());
        JSONObject stats = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_stats");

        JSONObject results = json.getJSONObject("data")
                .getJSONObject("attributes")
                .getJSONObject("last_analysis_results");

        int malicious = stats.getInt("malicious");
        int suspicious = stats.getInt("suspicious");

        // Step 5: costruisci dettagli dei motori antivirus
        StringBuilder details = new StringBuilder();
        for (String engine : results.keySet()) {
            JSONObject engineData = results.getJSONObject(engine);
            String category = engineData.getString("category");
            String result = engineData.optString("result", "unknown");
            if (category.equals("malicious") || category.equals("suspicious")) {
                details.append("- ").append(engine).append(": ").append(result).append("\n");
            }
        }

        // Step 6: interpreta il risultato combinando stats e dettagli
        if (malicious > 0) {
            return "❌ URL pericoloso!\nMotivi: " + malicious + " motori antivirus segnalano malware.\nDettagli:\n" + details.toString();
        } else {
            return "✅ URL sicuro.\nNessun motore antivirus lo segnala.";
        }
    }
}
