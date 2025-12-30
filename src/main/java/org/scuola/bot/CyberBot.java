package org.scuola.bot;

import org.scuola.bot.api.CheckIpService;
import org.scuola.bot.api.UrlScanService;
import org.scuola.bot.api.FileCheckService;
import org.scuola.bot.db.DatabaseManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

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
                send(chatId, "🔐 Benvenuto in SafeScan Bot!\n\n" +

                                "SafeScan è un bot di cybersecurity che ti aiuta a verificare " +
                                "se IP, siti web e file sono sicuri oppure potenzialmente dannosi, " +
                                "utilizzando il servizio di analisi VirusTotal.\n\n" +

                                "📌 Comandi disponibili:\n\n" +

                                "🖥️ /checkip <indirizzo IP>\n" +
                                "Controlla se un indirizzo IP è associato a malware, botnet o attività sospette.\n" +
                                "Esempio:\n" +
                                "✅ /checkip 8.8.8.8\n" +
                                "⚠ /checkip 185.220.101.1\n\n" +

                                "🌐 /checkurl <URL>\n" +
                                "Analizza un sito web per verificare se è segnalato come phishing, malware o sicuro.\n" +
                                "Esempio:\n" +
                                "✅ /checkurl https://www.google.com\n" +
                                "⚠ /checkurl http://testsafebrowsing.appspot.com/s/phishing.html\n\n" +

                                "📄 /checkfile <hash SHA-256>\n" +
                                "Verifica la sicurezza di un file tramite il suo hash SHA-256, senza caricare il file.\n" +
                                "Il bot dirà se il file è sicuro o sconosciuto.\n" +
                                "Se è sicuro vuol dire che non sono stati rilevati problemi, se è sconosciuto significa che non è presente nel database VirusTotal" +
                                "e non è mai stato analizzato. Per questo si consiglia di non eseguirlo senza prima averlo analizzato manualmente." +
                                "Esempio:\n" +
                                "❓ /checkfile 275a021bbfb6484f4e85a47f9aee3e7e18f00f2a8c6b8f4b8a8e24d0e6dfe8d5\n" +
                                "✅ /checkfile e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n\n" +

                                "📊 /stats\n" +
                                "Mostra quanti utenti hanno utilizzato il bot.\n\n" +

                                "Rimani al sicuro online 🔐" +
                                "\n" +
                                "- \uD83D\uDCCD /checkip IP\n" +
                                "- \uD83D\uDD0E /checkurl sito\n" +
                                "- \uD83D\uDCC1 /checkfile hash file\n" +
                                "- \uD83D\uDC64 /stats");
            }

            /* ===================== CHECK IP ===================== */
            else if (msg.startsWith("/checkip")) {
                String ip = msg.replace("/checkip", "").trim();

                if (!isValidIp(ip)) {
                    sendDog(chatId, 400, "❌ IP non valido.\nFormato richiesto: IPv4 o IPv6.");
                    return;
                }

                CheckIpService.CheckIpResult result = CheckIpService.check(ip);
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // LOG EVENTO
                DatabaseManager.logEvent(
                        chatId,
                        "IP",
                        ip,
                        result.getStatusCode(),
                        result.getStatusCode() == 200,
                        result.getStatusCode() == 200 ? null : "Segnalato da VirusTotal"
                );
            }

            /* ===================== CHECK URL ===================== */
            else if (msg.startsWith("/checkurl")) {
                String url = msg.replace("/checkurl", "").trim();

                if (!isValidUrl(url)) {
                    sendDog(chatId, 400,
                            "❌ URL non valido.\nDeve iniziare con http:// o https://");
                    return;
                }

                UrlScanService.UrlScanResult result = UrlScanService.scan(url);
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // LOG EVENTO
                DatabaseManager.logEvent(
                        chatId,
                        "URL",
                        url,
                        result.getStatusCode(),
                        result.getStatusCode() == 200,
                        result.getStatusCode() == 200 ? null : "URL segnalato come pericoloso"
                );
            }

            /* ===================== CHECK FILE ===================== */
            else if (msg.startsWith("/checkfile")) {
                String hash = msg.replace("/checkfile", "").trim();

                if (!isValidSha256(hash)) {
                    sendDog(chatId, 400,
                            "❌ Hash non valido.\nInserire un hash SHA-256 (64 caratteri).");
                    return;
                }

                FileCheckService.FileCheckResult result = FileCheckService.check(hash);
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // LOG EVENTO
                DatabaseManager.logEvent(
                        chatId,
                        "FILE",
                        hash,
                        result.getStatusCode(),
                        result.getStatusCode() == 200,
                        result.getStatusCode() == 200 ? null : "File segnalato o sconosciuto"
                );
            }

            /* ===================== STATS ===================== */
            else if (msg.startsWith("/stats")) {
                String stats = DatabaseManager.getUserStatsWithTotal(chatId);
                send(chatId, stats);
            }

        } catch (Exception e) {
            send(chatId, "❌ Errore: " + e.getMessage());
        }
    }

    private void send(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (Exception ignored) {}
    }

    //Utilizzo una seconda API in modo da associare i codici di stato a delle immagini
    private void sendDog(long chatId, int statusCode, String caption) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(String.valueOf(chatId));
            photo.setPhoto(new InputFile("https://http.dog/" + statusCode + ".jpg"));
            photo.setCaption(caption);
            execute(photo);
        } catch (Exception ignored) {}
    }

    private boolean isValidIp(String ip) {
        String ipv4 =
                "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" +
                        "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

        return ip.matches(ipv4);
    }

    private boolean isValidUrl(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getProtocol().equals("http") || u.getProtocol().equals("https");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidSha256(String hash) {
        return hash.matches("^[a-fA-F0-9]{64}$");
    }
}
