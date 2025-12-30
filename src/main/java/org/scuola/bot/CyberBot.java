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
                send(chatId, "Benvenuto in SafeScan Bot!\uD83D\uDD75\uFE0F\u200D♂\uFE0F \n" +
                                "\n" +
                                "SafeScan ti aiuta a verificare se IP e siti web sono\n" +
                                "⚠ pericolosi \n" +
                                "✅ sicuri \n" +
                                "utilizzando servizi di sicurezza affidabili.\n" +
                                "\n" +
                                "Comandi disponibili:\n" +
                                "/checkip <indirizzo IP>\n" +
                                "→ Controlla se un IP è associato a malware, spam o attività sospette.\n" +
                                "\n" +
                                "/checkurl <sito web>\n" +
                                "→ Analizza un URL per capire se è sicuro o dannoso.\n" +
                                "\n" +
                                "/stats\n" +
                                "→ Mostra quanti sono gli utenti registrati nel bot.\n" +
                                "\n" +
                                "Esempio:\n" +
                                "✅ /checkip 8.8.8.8\n" +
                                "⚠ /checkip 185.220.101.1\n" +
                                "✅ /checkurl http://example.net/login\n" +
                                "⚠ /checkurl http://testsafebrowsing.appspot.com/s/phishing.html\n" +
                                "\n" +
                                "Rimani al sicuro online \uD83D\uDD10\n" +
                                "\n" +
                                "- \uD83D\uDCCD /checkip IP\n" +
                                "- \uD83D\uDD0E /checkurl sito\n" +
                                "- \uD83D\uDC64 /stats");
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
