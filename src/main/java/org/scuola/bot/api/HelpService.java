package org.scuola.bot.api;

// Crea il messaggio visibile dopo aver digitato il comando /help
public class HelpService {

    public static String getHelpMessage() {
        return """
        📖 *Guida SafeScan Bot*
        
        📌 *Comandi disponibili*
        
        \uD83E\uDE77 /help
        Mostra questa guida in qualsiasi momento.
                 
        🙂 /example
        Mostra esempi pratici di utilizzo dei comandi
        
        🖥️ /checkip <indirizzo IP>
        Controlla se un IP è associato a malware, botnet o attività sospette.
        
        🌐 /checkurl <URL>
        Analizza un sito web per phishing, malware o contenuti dannosi.
        
        📄 /checkfile <hash SHA-256>
        Verifica un file usando il suo hash (senza caricarlo).
        
       🔎 /checkdomain <dominio>
       Analizza un dominio (senza http/https). 
        
        🌍 /myip
        Mostra il tuo IP pubblico e ti chiede se vuoi verificarlo.
        
        🌸 /createpw <lunghezza>
        Genera una password sicura casuale.
                        
        ⭐ /tips
        Mostra delle brevi frasi casuali che consigliano all'utente come rimanere al sicuro online.
        
        📊 /stats
        Mostra le tue statistiche e il numero totale di utenti.
        
        🔐 Rimani al sicuro online!
        """;
    }
}
