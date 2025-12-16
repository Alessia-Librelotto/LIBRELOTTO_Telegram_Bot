package org.scuola.bot.api;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Scanner;

public class PasswordCheckService {

    public static boolean check(String password) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte b : hash)
            sb.append(String.format("%02X", b));

        String sha1 = sb.toString();
        String prefix = sha1.substring(0,5);
        String suffix = sha1.substring(5);

        URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Scanner sc = new Scanner(con.getInputStream());
        while (sc.hasNext()) {
            if (sc.nextLine().startsWith(suffix))
                return true;
        }
        return false;
    }
}
