package org.scuola.bot.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyIpService {

    public static String getMyIp() throws Exception {
        URL url = new URL("https://api.ipify.org?format=json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        String response = br.readLine();
        br.close();

        JSONObject json = new JSONObject(response);
        return json.getString("ip");
    }
}
