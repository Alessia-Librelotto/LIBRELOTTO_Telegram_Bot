# SafeScan Bot 🔐

Bot Telegram di cybersecurity che analizza IP, URL, domini e file utilizzando l'API di VirusTotal per identificare potenziali minacce informatiche.

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-blue)
![SQLite](https://img.shields.io/badge/Database-SQLite-green)
![VirusTotal](https://img.shields.io/badge/API-VirusTotal-red)

## 📋 Descrizione del Progetto

SafeScan è un bot Telegram che permette di verificare la sicurezza di indirizzi IP, siti web, domini e file attraverso l'integrazione con l'API di VirusTotal. Il bot fornisce risposte visive utilizzando immagini correlate ai codici di stato HTTP e mantiene statistiche dettagliate sugli utenti e le analisi effettuate.

### Funzionalità Principali

- ✅ **Controllo IP**: Verifica se un indirizzo IP è associato a malware o se è sicuro
- ✅ **Analisi URL**: Controlla se un sito web è segnalato o contiene malware
- ✅ **Verifica Domini**: Analizza la reputazione di un dominio senza bisogno del protocollo http/https
- ✅ **Verifica File**: Analizza file tramite hash SHA-256 senza necessità di upload 
- ✅ **Trova il proprio IP**: Mostra l'IP pubblico dell'utente con opzione di verifica interattiva
- ✅ **Generatore Password**: Crea password sicure casuali con lunghezza personalizzabile
- ✅ **Consigli di Sicurezza**: Fornisce suggerimenti random per rimanere al sicuro online
- ✅ **Esempi di utilizzo dei comandi**:  Mostra esempi pratici di utilizzo dei comandi
- ✅ **Statistiche Utente**: Traccia gli utenti del bot e fornisce statistiche dettagliate
- ✅ **Database**: Memorizza informazioni su utenti ed eventi di sicurezza
- ✅ **Feedback Visivo**: Utilizza immagini correlate ai codici HTTP per rendere le risposte più intuitive

## 🔗 API Utilizzate

### VirusTotal API v3
- 📚 [Documentazione Ufficiale](https://developers.virustotal.com/reference/overview)
- 🔑 [Ottieni la tua API Key gratuita](https://www.virustotal.com/gui/join-us)

VirusTotal è un servizio che analizza file e URL sospetti per rilevare malware utilizzando oltre 70 motori antivirus diversi.

### ipify
- 🌍 Servizio gratuito per ottenere l'indirizzo IP pubblico del dispositivo
- API endpoint: `https://api.ipify.org?format=json`

### HTTP Status Dog
- 🐕 Fornisce immagini associate ai codici di stato HTTP
- Utilizzato per il feedback visivo: `https://http.dog/{statusCode}.jpg`

### 🧠 Processo tecnico

Il bot invia dati a VirusTotal tramite API, che:
- Controlla blacklist internazionali
- Analizza contenuti in tempo reale
- Confronta con database di malware noti
- Verifica tentativi di phishing
- Controlla la reputazione e la popolarità

Restituisce un report JSON dettagliato con:
- Numero di motori antivirus che segnalano minacce
- Categoria del rischio (malware, phishing, sospetto, sicuro)
- Statistiche della community
- Indici di popolarità e reputazione dei domini

### Endpoint VirusTotal Utilizzati

- `/ip_addresses/{ip}` - Analisi reputazione indirizzi IP
- `/urls/{id}` - Scansione sicurezza URL (con codifica Base64)
- `/files/{hash}` - Verifica hash file SHA-256
- `/domains/{domain}` - Analisi completa domini

## 🚀 Setup e Installazione

### Prerequisiti

- Java JDK 21 o superiore
- Maven 3.6+
- Account VirusTotal (per API key gratuita)
- Bot Telegram (creato tramite [@BotFather](https://t.me/botfather))

### Configurazione

```config.properties
BOT_TOKEN=inserisci_qui_il_token_del_bot
BOT_USERNAME=inserisci_qui_username_bot
VIRUSTOTAL_API_KEY=inserisci_qui_api_key_virustotal
```

## 📱 Guida all'Utilizzo

### Comandi Disponibili

| Comando                  | Descrizione                                          | Esempio                            |
|--------------------------|------------------------------------------------------|------------------------------------|
| `/start`                 | Avvia il bot e mostra il menù completo               | `/start`                           |
| `/help`                  | Mostra la guida di tutti i comandi                   | `/help`                            |
| `/example`               |  Mostra esempi pratici di utilizzo dei comandi       | `/example`
| `/checkip <IP>`          | Controlla la sicurezza di un indirizzo IP            | `/checkip 8.8.8.8`                 |
| `/checkurl <URL>`        | Analizza un sito web                                 | `/checkurl https://www.google.com` |
| `/checkdomain <dominio>` | Verifica la reputazione di un dominio                | `/checkdomain wikipedia.org`       |
| `/checkfile <hash>`      | Verifica un file tramite hash SHA-256                | `/checkfile e3b0c44...`            |
| `/myip`                  | Mostra il tuo IP pubblico con opzione di verifica    | `/myip`                            |
| `/createpw <lunghezza>`  | Genera una password sicura (default 16 caratteri)    | `/createpw 20`                     |
| `/tips`                  | Mostra un consiglio casuale di sicurezza informatica | `/tips`                            |
| `/stats`                 | Mostra le statistiche personali e totali             | `/stats`                           |

### Esempi Pratici

**Controllo IP Sicuro:**
```
Utente: /checkip 8.8.8.8
Bot: ✅ IP sicuro.
     Nessun motore antivirus lo segnala.
     [Immagine status 200]
```

**Controllo URL Pericoloso:**
```
Utente: /checkurl http://testsafebrowsing.appspot.com/s/phishing.html
Bot: ❌ URL pericoloso!
     Motivi: 15 motori antivirus segnalano malware.
     Dettagli:
     - Google Safebrowsing: phishing
     - Kaspersky: malicious
     [Immagine status 451]
```

**Controllo Dominio Sconosciuto:**
```
Utente: /checkdomain suspicious-site.net
Bot: ❓ Dominio sconosciuto
     Non ci sono informazioni sufficienti su questo dominio.
     ⚠️ Potrebbe non esistere o non essere mai stato visitato. Usalo con cautela.
     [Immagine status 404]
```

**Controllo Dominio Sicuro:**
```
Utente: /checkdomain wikipedia.org
Bot: ✅ Dominio sicuro.
     Analizzato positivamente da 75 motori antivirus.
     [Immagine status 200]
```

**Trova il Mio IP:**
```
Utente: /myip
Bot: 🌍 Il tuo IP pubblico è:
     203.0.113.42
     
     Vuoi verificare se è sicuro?
     [Bottone: ✅ Sì, verifica] [Bottone: ❌ No]
```

**Generazione Password:**
```
Utente: /createpw 20
Bot: 🔐 Password generata:
     
     X9k$mP2@vL#qR8nT&wY5
     
     ✔ Lunghezza: 20
     ✔ Include lettere, numeri e simboli
     
     ⚠ Non condividere mai questa password!
```

**Consiglio di Sicurezza:**
```
Utente: /tips
Bot: 💡 Consiglio di sicurezza:
     
     🔐 Usa password lunghe e uniche per ogni servizio.
```

**Statistiche:**
```
Utente: /stats
Bot: 👤 Informazioni utente:
     Telegram ID: 123456789
     Username: @esempio
     Comandi eseguiti: 42
     Primo accesso: 2025-01-15 10:30:00
     Ultimo accesso: 2025-01-20 14:22:00
     Ultimo comando: /stats
     
     👥 Totale utenti registrati: 127
```

## 🗄️ Schema Database

Il bot utilizza SQLite con due tabelle principali:

### Tabella USERS
Memorizza informazioni sugli utenti del bot.

| Campo | Tipo | Descrizione |
|-------|------|-------------|
| `telegram_id` | TEXT (PK) | ID univoco Telegram dell'utente |
| `username` | TEXT | Username Telegram |
| `checks` | INTEGER | Numero totale di comandi eseguiti |
| `first_seen` | TEXT | Data e ora primo utilizzo |
| `last_seen` | TEXT | Data e ora ultimo accesso |
| `last_command` | TEXT | Ultimo comando eseguito |

### Tabella EVENTS
Registra ogni controllo di sicurezza effettuato.

| Campo | Tipo | Descrizione |
|-------|------|-------------|
| `event_id` | INTEGER (PK, AUTOINCREMENT) | ID univoco evento |
| `telegram_id` | TEXT (FK) | Riferimento all'utente |
| `event_type` | TEXT | Tipo: IP, URL, FILE, DOMAIN, MYIP, PASSWORD, TIPS |
| `input_value` | TEXT | Valore analizzato |
| `status_code` | INTEGER | Codice risultato (200=sicuro, 300=sospetto, 451=pericoloso, etc.) |
| `is_safe` | INTEGER | 1=sicuro, 0=non sicuro |
| `risk_reason` | TEXT | Motivo della segnalazione |
| `created_at` | TEXT | Timestamp dell'evento (default: datetime locale) |

### Relazioni
- `events.telegram_id` → `users.telegram_id` (chiave esterna)

### Esempi di Query Implementate

1. **Registrazione/Aggiornamento Utente**
```sql
INSERT INTO users (telegram_id, username, checks, first_seen, last_seen, last_command)
VALUES (?, ?, 1, datetime('now','localtime'), datetime('now','localtime'), ?)
ON CONFLICT(telegram_id)
DO UPDATE SET 
    checks = checks + 1,
    username = excluded.username, 
    last_seen = datetime('now','localtime'),
    last_command = excluded.last_command
```

2. **Statistiche Utente con Totale**
```sql
SELECT * FROM users WHERE telegram_id = ?;
SELECT COUNT(*) AS totale FROM users;
```

3. **Log Eventi di Sicurezza**
```sql
INSERT INTO events 
(telegram_id, event_type, input_value, status_code, is_safe, risk_reason)
VALUES (?, ?, ?, ?, ?, ?)
```

## 🏗️ Architettura del Progetto

```
org.scuola.bot/
├── model/
│   ├── Main.java                    # Entry point dell'applicazione
│   ├── CyberBot.java               # Gestione comandi e logica principale
│   └── MyConfiguration.java        # Caricamento configurazione
├── db/
│   └── DatabaseManager.java        # Gestione database SQLite
└── api/
    ├── CheckIpService.java         # Servizio verifica IP
    ├── CheckDomainService.java     # Servizio verifica domini
    ├── UrlScanService.java         # Servizio scansione URL
    ├── FileCheckService.java       # Servizio controllo file
    ├── MyIpService.java            # Servizio recupero IP pubblico
    ├── PasswordGeneratorService.java # Generatore password sicure
    ├── SecurityTipsService.java    # Consigli di sicurezza
    └── HelpService.java            # Messaggio di aiuto
```

### Nuove Funzionalità nei Servizi

#### CheckDomainService
- Analizza domini senza bisogno del protocollo (http/https)
- Valuta reputazione, popolarità e voti della community
- Distingue tra: sicuro (200), pericoloso (451), sconosciuto (404)
- Richiede almeno 20+ motori che lo considerano sicuro E almeno uno tra: voti community, popolarità, reputazione positiva

#### FileCheckService
- Distingue tra file **sospetti** (300) e file **sicuri** (200)
- File sospetto: almeno un motore lo segnala come "suspicious"
- File sconosciuto: codice 404 se non presente nel database VirusTotal

#### MyIpService
- Utilizza API ipify per recuperare l'IP pubblico
- Integrato con inline keyboard per conferma utente

#### PasswordGeneratorService
- Genera password con caratteri minuscoli, maiuscoli, numeri e simboli
- Garantisce almeno un carattere per ogni tipo
- Lunghezza minima 8 caratteri, default 16

#### SecurityTipsService
- 10 consigli di sicurezza informatica
- Selezione casuale per varietà

### Codici di Stato HTTP Utilizzati

Il bot utilizza codici HTTP semantici con immagini associate (tramite http.dog):

- **200**: Risorsa sicura ✅
- **400**: Input non valido ❌
- **404**: File/risorsa sconosciuta ❓
- **451**: Contenuto pericoloso 🚫

## 🎮 Interattività con Inline Keyboard

Il comando `/myip` implementa una **inline keyboard** che permette all'utente di:
1. Visualizzare il proprio IP pubblico
2. Scegliere se verificarlo tramite VirusTotal
3. Ignorare la verifica

**Callback gestiti:**
- `CHECK_MY_IP:{ip}` - Verifica l'IP mostrato
- `IGNORE` - Ignora e mostra comandi disponibili

## 📦 Dipendenze Maven

```xml
<dependencies>
    <!-- Telegram Bots API -->
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots</artifactId>
        <version>6.9.7.1</version>
    </dependency>
    
    <!-- SQLite JDBC -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.45.0.0</version>
    </dependency>
    
    <!-- JSON Processing -->
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20240303</version>
    </dependency>
</dependencies>
```

## 🔄 Flusso di Esecuzione

1. **Ricezione Messaggio**: L'utente invia un comando
2. **Registrazione Utente**: Il database viene aggiornato automaticamente
3. **Validazione Input**: Controllo formato IP/URL/hash/dominio
4. **Chiamata API**: Richiesta a VirusTotal o ipify
5. **Analisi Risposta**: Parsing JSON e valutazione risultati
6. **Log Evento**: Salvataggio nel database
7. **Risposta Utente**: Invio messaggio + immagine http.dog

## 🛡️ Note sulla Sicurezza

- Le API key sono gestite tramite file di configurazione esterno
- Il database è locale e non espone dati sensibili
- Il bot non carica mai file, utilizza solo hash SHA-256
- Tutti gli input vengono validati prima dell'analisi
- I risultati si basano esclusivamente sui dati delle API

## 🚨 Gestione Errori

- **Input non valido**: Codice 400 con messaggio esplicativo
- **Errore API**: Messaggio generico di errore
- **File/Dominio non trovato**: Codice 404 con avviso di cautela

## 📈 Statistiche Tracciate

Per ogni utente:
- Numero totale di comandi eseguiti
- Data primo e ultimo accesso
- Ultimo comando utilizzato

Per il sistema:
- Totale utenti registrati
- Log completo di tutti gli eventi
- Storico delle analisi effettuate

## 👨‍💻 Autore

Alessia Librelotto

---
