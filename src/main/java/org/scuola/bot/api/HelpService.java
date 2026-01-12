package org.scuola.bot.api;

public class HelpService {

    public static String getHelpMessage() {
        return """
        📖 *Guida SafeScan Bot*
        
        📌 *Comandi disponibili*
        
        🖥️ /checkip <indirizzo IP>
        Controlla se un IP è associato a malware, botnet o attività sospette.
        
        🌐 /checkurl <URL>
        Analizza un sito web per phishing, malware o contenuti dannosi.
        
        📄 /checkfile <hash SHA-256>
        Verifica un file usando il suo hash (senza caricarlo).
        Se non è nel database VirusTotal risulterà “sconosciuto”.
        
       🔎 /checkdomain <dominio>
       Analizza un dominio (senza http/https). 
        
        🌸 /createpw <lunghezza>
        Genera una password sicura casuale.
                        
        ⭐ /tips
        Mostra delle brevi frasi casuali che consigliano all'utente come rimanere al sicuro online.
        
        🌍 /myip
        Mostra il tuo IP pubblico e ti chiede se vuoi verificarlo.
        
        📊 /stats
        Mostra le tue statistiche e il numero totale di utenti.
        
        ❓ /help
        Mostra questa guida in qualsiasi momento.
        
        🔐 Rimani al sicuro online!
        """;
    }
}
