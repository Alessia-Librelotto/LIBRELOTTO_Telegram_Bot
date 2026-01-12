package org.scuola.bot;

import org.scuola.bot.api.*;
import org.scuola.bot.db.DatabaseManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.scuola.bot.api.SecurityTipsService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

// Classe principale del bot Telegram che gestisce:
 // ricezione dei messaggi
 // chiamate alle API
 // logging nel database
 // invio delle risposte all'utente
public class CyberBot extends TelegramLongPollingBot {

    // Restituisce il token del bot (letto da file di configurazione)
    @Override
    public String getBotToken() {
        return MyConfiguration.get("BOT_TOKEN");
    }

    // Username pubblico del bot Telegram
    @Override
    public String getBotUsername() {
        return MyConfiguration.get("BOT_USERNAME");
    }

    // Metodo chiamato automaticamente ad ogni messaggio ricevuto
    @Override
    public void onUpdateReceived(Update update) {

        /* ===================== CALLBACK QUERY ===================== */
        if (update.hasCallbackQuery()) {

            String data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            try {
                // L'utente ha scelto di verificare il suo IP
                if (data.startsWith("CHECK_MY_IP:")) {
                    String ip = data.split(":")[1];

                    CheckIpService.CheckIpResult result =
                            CheckIpService.check(ip);

                    sendDog(chatId,
                            result.getStatusCode(),
                            result.getMessage());

                    DatabaseManager.logEvent(
                            chatId,
                            "IP",
                            ip,
                            result.getStatusCode(),
                            result.getStatusCode() == 200,
                            result.getStatusCode() == 200 ? null : "Segnalato da VirusTotal"
                    );
                }

                // L'utente ha scelto di NON verificare
                else if (data.equals("IGNORE")) {
                    send(chatId,
                            "👍 Va bene!\n" +
                                    "Se vuoi controllare qualcosa più tardi puoi usare:\n" +
                                    "/checkip <IP>\n" +
                                    "/checkurl <URL>\n" +
                                    "/checkfile <HASH>\n" +
                                    "/checkdomain <DOMAIN>");
                }

            } catch (Exception e) {
                send(chatId, "❌ Errore durante l'operazione.");
            }

            return;
        }

        /* ===================== MESSAGGI TESTO ===================== */
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
                        "\uD83D\uDCDE /start\n" +
                        "Avvia il bot\n\n" +
                        "\uD83E\uDE77 /help\n" +
                        "Visualizza tutti i comandi disponibili.\n\n"+
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
                        "e non è mai stato analizzato. Per questo si consiglia di non eseguirlo senza prima averlo analizzato manualmente.\n" +
                        "Esempio:\n" +
                        "❓ /checkfile 275a021bbfb6484f4e85a47f9aee3e7e18f00f2a8c6b8f4b8a8e24d0e6dfe8d5\n" +
                        "✅ /checkfile e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n\n" +
                        "\uD83D\uDD0E /checkdomain <dominio>\n" +
                                "Analizza un dominio (senza http/https).\n" +
                                "Esempio:\n" +
                                "✅ /checkdomain wikipedia.org \n" +
                                "❓ /checkdomain suspicious-site.net\n" +
                                "⚠ /checkdomain google.com\n\n" +
                        "\uD83D\uDCE1 /myip\n" +
                                "Mostra il tuo indirizzo IP pubblico e ti permette di verificare\n" +
                                "se è sicuro o potenzialmente dannoso.\n\n" +
                        "⭐ /tips\n" +
                        "Mostra delle brevi frasi casuali che consigliano all'utente come rimanere al sicuro online.\n\n" +
                        "📊 /stats\n" + "Mostra quanti utenti hanno utilizzato il bot.\n\n" +
                        "Rimani al sicuro online 🔐" + "\n" + "- \uD83E\uDE77 /help\n" + "- \uD83D\uDCCD /checkip IP\n" + "- \uD83D\uDD0E /checkurl sito\n" + "- \uD83D\uDCC1 /checkfile hash file\n" + "- 🌐 /checkdomain dominio\n" + "- \uD83D\uDCE1 /myip\n" + "- ⭐ /tips\n" + "- \uD83D\uDC64 /stats"); }

            /* ===================== HELP ===================== */
            else if (msg.startsWith("/help")) {
                send(chatId, HelpService.getHelpMessage());
            }

            /* ===================== CHECK IP ===================== */
            else if (msg.startsWith("/checkip")) {
                // Estrae l'IP dal messaggio
                String ip = msg.replace("/checkip", "").trim();

                // Validazione input IP
                if (!isValidIp(ip)) {
                    sendDog(chatId, 400, "❌ IP non valido.\nFormato richiesto: IPv4 o IPv6.");
                    return;
                }

                // Chiamata all'API VirusTotal
                CheckIpService.CheckIpResult result = CheckIpService.check(ip);

                // Invio risultato + immagine associata al codice HTTP
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // Log dell'evento nel database
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
                // Estrae l'URL
                String url = msg.replace("/checkurl", "").trim();

                // Validazione URL
                if (!isValidUrl(url)) {
                    sendDog(chatId, 400,
                            "❌ URL non valido.\nDeve iniziare con http:// o https://");
                    return;
                }

                // Chiamata all'API VirusTotal
                UrlScanService.UrlScanResult result = UrlScanService.scan(url);

                // Invio risposta all'utente
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // Log dell'evento
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
                // Estrae hash del file
                String hash = msg.replace("/checkfile", "").trim();

                // Validazione SHA-256
                if (!isValidSha256(hash)) {
                    sendDog(chatId, 400,
                            "❌ Hash non valido.\nInserire un hash SHA-256 (64 caratteri).");
                    return;
                }

                // Chiamata all'API VirusTotal
                FileCheckService.FileCheckResult result = FileCheckService.check(hash);

                // Risposta all'utente
                sendDog(chatId, result.getStatusCode(), result.getMessage());

                // Log evento nel database
                DatabaseManager.logEvent(
                        chatId,
                        "FILE",
                        hash,
                        result.getStatusCode(),
                        result.getStatusCode() == 200,
                        result.getStatusCode() == 200 ? null : "File segnalato o sconosciuto"
                );
            }

            /* ===================== CHECK DOMAIN ===================== */
            else if (msg.startsWith("/checkdomain")) {

                String domain = msg.replace("/checkdomain", "").trim();

                if (domain.isEmpty() || domain.contains("://")) {
                    sendDog(chatId, 400,
                            "❌ Dominio non valido.\nEsempio corretto:\n/checkdomain google.com");
                    return;
                }

                CheckDomainService.DomainCheckResult result =
                        CheckDomainService.check(domain);

                sendDog(chatId, result.getStatusCode(), result.getMessage());

                DatabaseManager.logEvent(
                        chatId,
                        "DOMAIN",
                        domain,
                        result.getStatusCode(),
                        result.getStatusCode() == 200,
                        result.getStatusCode() == 200 ? null : "Dominio segnalato"
                );
            }

            /* ===================== MY IP ===================== */
            else if (msg.equals("/myip")) {
                String ip = MyIpService.getMyIp();
                askToCheckIp(chatId, ip);
            }

            /* ===================== TIPS ===================== */
            else if (msg.equals("/tips")) {
                String tip = SecurityTipsService.getRandomTip();
                send(chatId, "💡 Consiglio di sicurezza:\n\n" + tip);
            }

            /* ===================== STATS ===================== */
            else if (msg.startsWith("/stats")) {
                // Recupera statistiche utente + totale utenti
                String stats = DatabaseManager.getUserStatsWithTotal(chatId);
                send(chatId, stats);
            }

        } catch (Exception e) {
            // Gestione errori generici
            send(chatId, "❌ Errore: " + e.getMessage());
        }
    }

    // Invia un messaggio di testo semplice
    private void send(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (Exception ignored) {}
    }

    // Utilizzo una seconda API in modo da associare i codici di stato a delle immagini
    private void sendDog(long chatId, int statusCode, String caption) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(String.valueOf(chatId));
            photo.setPhoto(new InputFile("https://http.dog/" + statusCode + ".jpg"));
            photo.setCaption(caption);
            execute(photo);
        } catch (Exception ignored) {}
    }

    // Valida un indirizzo IPv4 tramite regex
    private boolean isValidIp(String ip) {
        String ipv4 =
                "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" +
                        "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

        return ip.matches(ipv4);
    }

    // Controlla che l'URL sia ben formato e usi http/https
    private boolean isValidUrl(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getProtocol().equals("http") || u.getProtocol().equals("https");
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica che l'hash sia un SHA-256 valido
    private boolean isValidSha256(String hash) {
        return hash.matches("^[a-fA-F0-9]{64}$");
    }

     private void askToCheckIp(long chatId, String ip) {
         InlineKeyboardButton yes = new InlineKeyboardButton("✅ Sì, verifica");
         yes.setCallbackData("CHECK_MY_IP:" + ip);

         InlineKeyboardButton no = new InlineKeyboardButton("❌ No");
         no.setCallbackData("IGNORE");

         InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                 java.util.List.of(
                         java.util.List.of(yes, no)
                 )
         );

         SendMessage msg = new SendMessage();
         msg.setChatId(String.valueOf(chatId));
         msg.setText("🌍 Il tuo IP pubblico è:\n" + ip + "\n\nVuoi verificare se è sicuro?");
         msg.setReplyMarkup(markup);

         try {
             execute(msg);
         } catch (Exception ignored) {}
     }
 }
