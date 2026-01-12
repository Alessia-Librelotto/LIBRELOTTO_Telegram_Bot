package org.scuola.bot.api;

import org.json.JSONObject;
import org.scuola.bot.MyConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Dato un DOMAIN valido stabilisce se è sicuro, dannoso o sconosciuto
// Il controllo viene effettuato tramite l'API VirusTotal
public class CheckDomainService {

    public static DomainCheckResult check(String domain) throws Exception {

        URL url = new URL("https://www.virustotal.com/api/v3/domains/" + domain);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("x-apikey",
                MyConfiguration.get("VIRUSTOTAL_API_KEY"));

        int httpResponseCode = con.getResponseCode();

        if (httpResponseCode == 404) {
            return new DomainCheckResult(404,
                    "❓ Dominio sconosciuto\n" +
                            "Questo dominio non esiste nel database di VirusTotal.\n" +
                            "⚠️ Usalo con molta cautela.");
        }

        if (httpResponseCode != 200) {
            return new DomainCheckResult(httpResponseCode,
                    "⚠️ Errore nella richiesta a VirusTotal (codice " + httpResponseCode + ")");
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        JSONObject json = new JSONObject(sb.toString());
        JSONObject attributes = json.getJSONObject("data").getJSONObject("attributes");

        JSONObject stats = attributes.getJSONObject("last_analysis_stats");

        int malicious = stats.getInt("malicious");
        int suspicious = stats.getInt("suspicious");
        int harmless = stats.getInt("harmless");

        // Reputation
        int reputation = attributes.optInt("reputation", 0);

        // Voti community (SEGNALE AFFIDABILE)
        JSONObject totalVotes = attributes.getJSONObject("total_votes");
        int harmlessVotes = totalVotes.getInt("harmless");
        int maliciousVotes = totalVotes.getInt("malicious");
        int totalCommunityVotes = harmlessVotes + maliciousVotes;

        // Popularity ranks (SEGNALE AFFIDABILE)
        boolean hasPopularity = attributes.getJSONObject("popularity_ranks").length() > 0;

        int statusCode;
        String message;

        // ❌ PERICOLOSO
        if (malicious > 0) {
            statusCode = 451;
            message = "❌ Dominio pericoloso!\n" +
                    "Segnalato come malevolo da " + malicious + " motori antivirus.";
        }

        // 🔑 SICURO solo se:
        // 1. harmless alto (> 20)
        // 2. E almeno UNO tra:
        //    - persone reali lo hanno visitato
        //    - è un sito popolare
        //    - Ha reputation positiva (> 0)
        else if (harmless > 20 && (totalCommunityVotes > 0 || hasPopularity || reputation > 0)) {
            statusCode = 200;
            message = "✅ Dominio sicuro.\n" +
                    "Analizzato positivamente da " + harmless + " motori antivirus.";
        }

        // ❓ SCONOSCIUTO - tutto il resto
        else {
            statusCode = 404;
            message = "❓ Dominio sconosciuto\n" +
                    "Non ci sono informazioni sufficienti su questo dominio.\n" +
                    "⚠️ Potrebbe non esistere o non essere mai stato visitato. Usalo con cautela.";
        }

        return new DomainCheckResult(statusCode, message);
    }

    public static class DomainCheckResult {
        private final int statusCode;
        private final String message;

        public DomainCheckResult(int statusCode, String message) {
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