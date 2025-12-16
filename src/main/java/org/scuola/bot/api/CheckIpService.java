package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckIpService {

    public static String check(String ip) throws Exception {

        // Endpoint VirusTotal per IP
        URL url = new URL("https://www.virustotal.com/api/v3/ip_addresses/" + ip);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
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

            if (category.equals("malicious") || category.equals("suspicious")) {
                details.append("- ").append(engine)
                        .append(": ").append(result).append("\n");
            }
        }

        // Risultato finale
        if (malicious > 0) {
            return "❌ IP pericoloso!\n" +
                    "Motivi: " + malicious + " motori antivirus lo segnalano.\n" +
                    "Dettagli:\n" + details;
        } else if (suspicious > 0) {
            return "⚠️ IP sospetto.\n" +
                    "Alcuni motori antivirus lo considerano rischioso.\n" +
                    "Dettagli:\n" + details;
        } else {
            return "✅ IP sicuro.\nNessun motore antivirus lo segnala.";
        }
    }
}
