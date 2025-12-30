package org.scuola.bot.db;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:cyberbot.db";

    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement s = c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                telegram_id TEXT PRIMARY KEY,
                username TEXT,
                checks INTEGER DEFAULT 0,
                first_seen TEXT,
                last_seen TEXT,
                last_command TEXT
            )
            """);

        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void registerUser(long telegramId, String username, String lastCommand)
    throws Exception {
        Connection c = DriverManager.getConnection(URL);

        PreparedStatement ps = c.prepareStatement("""
    INSERT INTO users (telegram_id, username, checks, first_seen, last_seen, last_command)
    VALUES (?, ?, 1, datetime('now','localtime'), datetime('now','localtime'), ?)
    ON CONFLICT(telegram_id)
    DO UPDATE SET
        checks = checks + 1,
        username = excluded.username,
        last_seen = datetime('now','localtime'),
        last_command = excluded.last_command
""");


        ps.setString(1, String.valueOf(telegramId));
        ps.setString(2, username);
        ps.setString(3, lastCommand);

        ps.executeUpdate();
        ps.close();
        c.close();
    }

    public static String getUserStatsWithTotal(long telegramId) throws Exception {
        Connection c = DriverManager.getConnection(URL);

        // Ottieni dettagli dell'utente
        PreparedStatement psUser = c.prepareStatement("SELECT * FROM users WHERE telegram_id = ?");
        psUser.setString(1, String.valueOf(telegramId));
        ResultSet rsUser = psUser.executeQuery();

        String userInfo;
        if (rsUser.next()) {
            userInfo = "👤 Informazioni utente:\n" +
                    "Telegram ID: " + rsUser.getString("telegram_id") + "\n" +
                    "Username: @" + rsUser.getString("username") + "\n" +
                    "Comandi eseguiti: " + rsUser.getInt("checks") + "\n" +
                    "Primo accesso: " + rsUser.getString("first_seen") + "\n" +
                    "Ultimo accesso: " + rsUser.getString("last_seen") + "\n" +
                    "Ultimo comando: " + rsUser.getString("last_command");
        } else {
            userInfo = "❌ Utente non trovato nel database.";
        }

        rsUser.close();
        psUser.close();

        // Ottieni numero totale utenti registrati
        Statement s = c.createStatement();
        ResultSet rsTotal = s.executeQuery("SELECT COUNT(*) AS totale FROM users");
        rsTotal.next();
        int totale = rsTotal.getInt("totale");
        rsTotal.close();
        s.close();

        c.close();

        return userInfo + "\n\n👥 Totale utenti registrati: " + totale;
    }



}
