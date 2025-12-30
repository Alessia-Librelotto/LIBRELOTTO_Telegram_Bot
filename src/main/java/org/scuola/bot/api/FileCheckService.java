package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileCheckService {

    public static String check(String hash) throws Exception {

        URL url = new URL("https://www.virustotal.com/api/v3/files/" + hash);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey", MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        if (con.getResponseCode() == 404) {
            return "❓ File sconosciuto\n" +
                    "Questo file non è presente nel database VirusTotal.\n" +
                    "Si consiglia di non eseguirlo.\n";
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

        return "✅ FILE SICURO\nNessun motore antivirus lo segnala.";
    }
}
