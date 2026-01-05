package org.scuola.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    // Metodo principale dell'applicazione che viene eseguito all'avvio del programma
    public static void main(String[] args) throws Exception {

        // Inizializza l'API Telegram usando una sessione di default
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

        // Registra il bot (CyberBot estende TelegramLongPollingBot)
        api.registerBot(new CyberBot());

        // Messaggio di conferma sul terminale
        System.out.println("Bot avviato con successo!");
    }
}
