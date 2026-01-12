package org.scuola.bot.db;

import java.sql.*;

// Gestisce la connessione al database SQLite
public class DatabaseManager {

    // URL del database SQLite (file locale)
    private static final String URL = "jdbc:sqlite:cyberbot.db";


     // Blocco statico: viene eseguito UNA SOLA VOLTA all'avvio dell'applicazione.
     // Serve per creare le tabelle se non esistono.
    static {
        try (Connection c = DriverManager.getConnection(URL);
             Statement s = c.createStatement()) {

            // Tabella USERS
            // Contiene le informazioni sugli utenti Telegram
            s.execute("""
            CREATE TABLE IF NOT EXISTS users (
                telegram_id TEXT PRIMARY KEY,      -- ID univoco dell'utente Telegram
                username TEXT,                     -- Username Telegram
                checks INTEGER DEFAULT 0,           -- Numero di comandi eseguiti
                first_seen TEXT,                    -- Primo utilizzo del bot
                last_seen TEXT,                     -- Ultimo utilizzo
                last_command TEXT                  -- Ultimo comando eseguito
            )
        """);

            // Tabella EVENTS
            // Contiene lo storico di tutte le operazioni eseguite dagli utenti
            s.execute("""
            CREATE TABLE IF NOT EXISTS events (
                event_id INTEGER PRIMARY KEY AUTOINCREMENT, -- ID evento
                telegram_id TEXT NOT NULL,                  -- Utente che ha eseguito l'azione
                event_type TEXT NOT NULL,                   -- Tipo evento: IP, URL, FILE
                input_value TEXT NOT NULL,                  -- Valore analizzato
                status_code INTEGER NOT NULL,               -- Codice di stato (200, 451, 404...)
                is_safe INTEGER NOT NULL,                   -- 1 = sicuro, 0 = pericoloso
                risk_reason TEXT,                            -- Motivo del rischio (se presente)
                created_at TEXT DEFAULT (datetime('now','localtime')), -- Timestamp
                FOREIGN KEY (telegram_id) REFERENCES users(telegram_id)
            )
        """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     // Registra o aggiorna un utente.
     // Se l'utente è nuovo: viene inserito
     // Se esiste già: aggiorna ultimo accesso e incrementa il contatore
    public static void registerUser(long telegramId, String username, String lastCommand)
            throws Exception {

        Connection c = DriverManager.getConnection(URL);

        // INSERT
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

    // Restituisce: le informazioni dell'utente corrente e il numero totale di utenti registrati
    public static String getUserStatsWithTotal(long telegramId) throws Exception {
        Connection c = DriverManager.getConnection(URL);

        // Ottieni dettagli dell'utente
        PreparedStatement psUser = c.prepareStatement(
                "SELECT * FROM users WHERE telegram_id = ?"
        );
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
            userInfo = "❌ Utente non trovato nel database";
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

    // Registra un evento nel database.
    // Viene chiamato dopo ogni controllo (IP, URL, FILE, DOMAIN)
    public static void logEvent(
            long telegramId,
            String eventType,     // IP, URL, FILE, DOMAIN
            String inputValue,    // Valore analizzato
            int statusCode,       // Codice di stato
            boolean isSafe,       // true = sicuro, false = pericoloso
            String riskReason     // Motivo del rischio (opzionale)
    ) throws Exception {

        Connection c = DriverManager.getConnection(URL);

        PreparedStatement ps = c.prepareStatement("""
        INSERT INTO events 
        (telegram_id, event_type, input_value, status_code, is_safe, risk_reason)
        VALUES (?, ?, ?, ?, ?, ?)
    """);

        ps.setString(1, String.valueOf(telegramId));
        ps.setString(2, eventType);
        ps.setString(3, inputValue);
        ps.setInt(4, statusCode);
        ps.setInt(5, isSafe ? 1 : 0); // SQLite non ha booleani
        ps.setString(6, riskReason);

        ps.executeUpdate();
        ps.close();
        c.close();
    }
}