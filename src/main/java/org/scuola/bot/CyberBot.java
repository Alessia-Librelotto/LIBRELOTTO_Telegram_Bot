package org.scuola.bot;

import org.scuola.bot.api.CheckIpService;
import org.scuola.bot.api.PasswordCheckService;
import org.scuola.bot.api.UrlScanService;
import org.scuola.bot.db.DatabaseManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CyberBot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return MyConfiguration.get("BOT_TOKEN");
    }

    @Override
    public String getBotUsername() {
        return MyConfiguration.get("BOT_USERNAME");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        String msg = update.getMessage().getText();
        String command = msg.split(" ")[0];

        try {
            DatabaseManager.registerUser(chatId, username, command);

            if (msg.startsWith("/start")) {
                send(chatId, "Benvenuto in SafeScan🔐\n" +
                        "Il bot realizzato permette di controllare lo stato di IP e URL in modo da capire se sono " +
                        "sicuri oppure dannosi\n" +
                        "/checkip IP\n/checkurl sito\n/stats");
            }

            else if (msg.startsWith("/checkip")) {
                String ip = msg.replace("/checkip", "").trim();
                String risultato = CheckIpService.check(ip);
                send(chatId, risultato);
            }


            else if (msg.startsWith("/checkurl")) {
                String url = msg.replace("/checkurl", "").trim();
                String risultato = UrlScanService.scan(url);
                send(chatId, risultato);
            }

            else if (msg.startsWith("/stats")) {
                send(chatId, DatabaseManager.getStats());
            }

        } catch (Exception e) {
            send(chatId, "Errore: " + e.getMessage());
        }
    }

    private void send(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (Exception ignored) {}
    }
}
