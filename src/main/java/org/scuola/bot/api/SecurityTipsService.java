package org.scuola.bot.api;

import java.util.List;
import java.util.Random;

// Servizio che fornisce consigli di sicurezza informatica
// Ogni chiamata restituisce un suggerimento casuale
public class SecurityTipsService {

    // Lista di consigli di cybersecurity
    // I messaggi sono pronti per essere inviati all'utente
    private static final List<String> TIPS = List.of(
            "🔐 Usa password lunghe e uniche per ogni servizio.",
            "📧 Non aprire allegati email da mittenti sconosciuti.",
            "🌐 Controlla sempre che i siti inizino con https://",
            "🛑 Non inserire credenziali in pagine raggiunte da link sospetti.",
            "🧠 Se qualcosa sembra troppo bello per essere vero, probabilmente è una truffa.",
            "🧩 Mantieni sempre aggiornato il sistema operativo.",
            "🔑 Attiva l'autenticazione a due fattori (2FA) quando possibile.",
            "📁 Non eseguire file scaricati da fonti non affidabili.",
            "🧪 Se un file o dominio è sconosciuto, analizzalo prima di usarlo.",
            "📱 Installa app solo da store ufficiali."
    );

    // Generatore di numeri casuali
    private static final Random RANDOM = new Random();

    // Restituisce un consiglio casuale dalla lista
    // Usato dal comando /tips
    public static String getRandomTip() {

        // Seleziona un indice casuale valido
        // e ritorna il messaggio corrispondente
        return TIPS.get(
                RANDOM.nextInt(TIPS.size())
        );
    }
}
