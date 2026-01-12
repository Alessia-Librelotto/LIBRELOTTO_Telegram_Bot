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

    // Metodo principale che esegue il controllo del dominio
    public static DomainCheckResult check(String domain) throws Exception {

        // Endpoint VirusTotal per l'analisi dei domini
        URL url = new URL("https://www.virustotal.com/api/v3/domains/" + domain);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Imposta metodo HTTP GET
        con.setRequestMethod("GET");

        // Inserisce l' API Key di VirusTotal nell'header
        con.setRequestProperty(
                "x-apikey",
                MyConfiguration.get("VIRUSTOTAL_API_KEY")
        );

        // Codice HTTP restituito da VirusTotal
        int httpResponseCode = con.getResponseCode();

        // Se VirusTotal restituisce 404 significa che il dominio
        // non è mai stato analizzato o non esiste nel loro database
        if (httpResponseCode == 404) {
            return new DomainCheckResult(
                    404,
                    "❓ Dominio sconosciuto\n" +
                            "Questo dominio non esiste nel database di VirusTotal.\n" +
                            "⚠️ Usalo con molta cautela."
            );
        }

        // Se il codice HTTP non è 200 e non è 404
        // significa che c'è stato un errore nella richiesta
        if (httpResponseCode != 200) {
            return new DomainCheckResult(
                    httpResponseCode,
                    "⚠️ Errore nella richiesta a VirusTotal (codice " + httpResponseCode + ")"
            );
        }

        // Lettura della risposta JSON restituita dall'API
        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        StringBuilder sb = new StringBuilder();
        String line;

        // Legge tutta la risposta riga per riga
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // Parsing del JSON completo
        JSONObject json = new JSONObject(sb.toString());

        // Estrae la sezione "attributes" che contiene i dati principali
        JSONObject attributes = json
                .getJSONObject("data")
                .getJSONObject("attributes");

        // Statistiche finali dell'analisi
        JSONObject stats = attributes.getJSONObject("last_analysis_stats");

        // Numero di motori che segnalano il dominio
        int malicious = stats.getInt("malicious");
        int harmless = stats.getInt("harmless");

        // Reputation del dominio (può essere negativa, zero o positiva)
        int reputation = attributes.optInt("reputation", 0);

        // Voti della community VirusTotal (utenti reali)
        // Questo è un segnale affidabile
        JSONObject totalVotes = attributes.getJSONObject("total_votes");
        int harmlessVotes = totalVotes.getInt("harmless");
        int maliciousVotes = totalVotes.getInt("malicious");

        // Totale voti community
        int totalCommunityVotes = harmlessVotes + maliciousVotes;

        // Popularity ranks: indica se il sito è conosciuto/popolarmente visitato
        boolean hasPopularity =
                attributes.getJSONObject("popularity_ranks").length() > 0;

        int statusCode;
        String message;

        // ❌ PERICOLOSO
        // Se almeno un motore antivirus lo segnala come malevolo
        if (malicious > 0) {
            statusCode = 451;
            message = "❌ Dominio pericoloso!\n" +
                    "Segnalato come malevolo da " + malicious + " motori antivirus.";
        }

        // 🔑 SICURO solo se:
        // 1. Molti motori lo considerano harmless (> 20)
        // 2. E almeno uno di questi segnali è presente:
        //    - voti della community
        //    - sito popolare
        //    - reputazione positiva
        else if (
                harmless > 20 &&
                        (totalCommunityVotes > 0 || hasPopularity || reputation > 0)
        ) {
            statusCode = 200;
            message = "✅ Dominio sicuro.\n" +
                    "Analizzato positivamente da " + harmless + " motori antivirus.";
        }

        // ❓ SCONOSCIUTO
        // Tutti gli altri casi finiscono qui:
        // - dominio nuovo
        // - dominio poco usato
        // - informazioni insufficienti
        else {
            statusCode = 404;
            message = "❓ Dominio sconosciuto\n" +
                    "Non ci sono informazioni sufficienti su questo dominio.\n" +
                    "⚠️ Potrebbe non esistere o non essere mai stato visitato. Usalo con cautela.";
        }

        // Ritorna il risultato finale incapsulato
        return new DomainCheckResult(statusCode, message);
    }

    // Classe di supporto che incapsula il risultato del controllo
    public static class DomainCheckResult {

        // Codice di stato (200, 404, 451…)
        private final int statusCode;

        // Messaggio testuale per l'utente
        private final String message;

        public DomainCheckResult(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        // Getter del codice di stato
        public int getStatusCode() {
            return statusCode;
        }

        // Getter del messaggio
        public String getMessage() {
            return message;
        }
    }
}
