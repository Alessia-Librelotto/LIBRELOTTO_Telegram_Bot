package org.scuola.bot.db;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:cyberbot.db";

    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement s = c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    telegram_id TEXT,
                    checks INTEGER
                )
            """);

        } catch (Exception e) { e.printStackTrace(); }
    }

    public static String getStats() throws Exception {
        Connection c = DriverManager.getConnection(URL);
        Statement s = c.createStatement();

        ResultSet rs = s.executeQuery("SELECT COUNT(*) AS totale FROM users");

        return "👥 Utenti registrati: " + rs.getInt("totale");
    }
}
