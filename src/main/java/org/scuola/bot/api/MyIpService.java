package org.scuola.bot.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Servizio che recupera l'IP pubblico del computer
// su cui è in esecuzione il bot
public class MyIpService {

    // Metodo che restituisce l'IP pubblico come stringa
    public static String getMyIp() throws Exception {

        // URL del servizio esterno ipify
        // Restituisce l'IP pubblico in formato JSON
        URL url = new URL("https://api.ipify.org?format=json");

        // Apertura della connessione HTTP
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Impostazione del metodo HTTP GET
        con.setRequestMethod("GET");

        // Lettura della risposta restituita dal servizio
        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        // La risposta è composta da una sola riga JSON
        // Esempio: {"ip":"123.45.67.89"}
        String response = br.readLine();
        br.close();

        // Parsing della risposta JSON
        JSONObject json = new JSONObject(response);

        // Estrazione del campo "ip" dal JSON
        // che rappresenta l'IP pubblico
        return json.getString("ip");
    }
}
