# SafeScan Bot 🔐

Bot Telegram di cybersecurity che analizza IP, URL e file utilizzando l'API di VirusTotal per identificare potenziali minacce informatiche.

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-blue)
![SQLite](https://img.shields.io/badge/Database-SQLite-green)
![VirusTotal](https://img.shields.io/badge/API-VirusTotal-red)

## 📋 Descrizione del Progetto

SafeScan è un bot Telegram che permette di verificare la sicurezza di indirizzi IP, siti web e file attraverso l'integrazione con l'API di VirusTotal. Il bot fornisce risposte visive utilizzando immagini correlate ai codici di stato HTTP e mantiene statistiche dettagliate sugli utenti e le analisi effettuate.

### Funzionalità Principali

- ✅ **Controllo IP**: Verifica se un indirizzo IP è associato a malware o attività sospette
- ✅ **Analisi URL**: Controlla se un sito web è segnalato come phishing o contiene malware
- ✅ **Verifica File**: Analizza file tramite hash SHA-256 senza necessità di upload
- ✅ **Statistiche Utente**: Traccia l'utilizzo del bot e fornisce statistiche dettagliate
- ✅ **Database Persistente**: Memorizza informazioni su utenti ed eventi di sicurezza
- ✅ **Feedback Visivo**: Utilizza immagini correlate ai codici HTTP per rendere le risposte più intuitive

## 🔗 API Utilizzata

**VirusTotal API v3**
- 📚 [Documentazione Ufficiale](https://developers.virustotal.com/reference/overview)
- 🔑 [Ottieni la tua API Key gratuita](https://www.virustotal.com/gui/join-us)

VirusTotal è un servizio che analizza file e URL sospetti per rilevare malware utilizzando oltre 70 motori antivirus diversi.

### Endpoint Utilizzati

- `/ip_addresses/{ip}` - Analisi reputazione indirizzi IP
- `/urls/{id}` - Scansione sicurezza URL
- `/files/{hash}` - Verifica hash file SHA-256

## 🚀 Setup e Installazione

### Prerequisiti

- Java JDK 21 o superiore
- Maven 3.6+
- Account VirusTotal (per API key gratuita)
- Bot Telegram (creato tramite [@BotFather](https://t.me/botfather))

### Configurazione

Creazione di un file `config.properties` nella cartella `src/main/resources/`:

```properties
BOT_TOKEN=inserisci_qui_il_token_del_bot
BOT_USERNAME=inserisci_qui_username_bot
VIRUSTOTAL_API_KEY=inserisci_qui_api_key_virustotal
```

**Come ottenere le credenziali:**

- **BOT_TOKEN**: Crea un bot su Telegram tramite [@BotFather](https://t.me/botfather) e riceverai il token
- **BOT_USERNAME**: L'username del bot scelto durante la creazione 
- **VIRUSTOTAL_API_KEY**: Registrati su [VirusTotal](https://www.virustotal.com/gui/join-us) e ottieni la chiave gratuita dalla sezione API Key del tuo profilo

Il database SQLite (`cyberbot.db`) verrà creato automaticamente al primo avvio.

## 📱 Guida all'Utilizzo

### Comandi Disponibili

| Comando | Descrizione | Esempio |
|---------|-------------|---------|
| `/start` | Avvia il bot e mostra il menu completo | `/start` |
| `/checkip <IP>` | Controlla la sicurezza di un indirizzo IP | `/checkip 8.8.8.8` |
| `/checkurl <URL>` | Analizza un sito web | `/checkurl https://www.google.com` |
| `/checkfile <hash>` | Verifica un file tramite hash SHA-256 | `/checkfile e3b0c44...` |
| `/stats` | Mostra le statistiche personali e totali | `/stats` |

### Esempi 

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

**File Sconosciuto:**
```
Utente: /checkfile 275a021bbfb6484f4e85a47f9aee3e7e18f00f2a8c6b8f4b8a8e24d0e6dfe8d5
Bot: ❓ File sconosciuto
     Questo file non è presente nel database VirusTotal.
     Si consiglia di non eseguirlo.
     [Immagine status 404]
```

**Statistiche:**
```
Utente: /stats
Bot: 👤 Informazioni utente:
     Telegram ID: 123456789
     Username: @mario_rossi
     Comandi eseguiti: 15
     Primo accesso: 2025-01-15 10:30:00
     Ultimo accesso: 2025-01-20 14:22:00
     Ultimo comando: /checkip
     
     👥 Totale utenti registrati: 47
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
| `event_type` | TEXT | Tipo di controllo: IP, URL, FILE |
| `input_value` | TEXT | Valore analizzato |
| `status_code` | INTEGER | Codice risultato (200=sicuro, 451=pericoloso, etc.) |
| `is_safe` | INTEGER | 1=sicuro, 0=non sicuro |
| `risk_reason` | TEXT | Motivo della segnalazione |
| `created_at` | TEXT | Timestamp dell'evento |

### Relazioni
- `events.telegram_id` → `users.telegram_id` (chiave esterna)

### Esempi di Query Implementate

Il bot implementa diverse query per statistiche e analisi:

1. **Registrazione/Aggiornamento Utente**
```sql
INSERT INTO users (telegram_id, username, checks, first_seen, last_seen, last_command)
VALUES (?, ?, 1, datetime('now','localtime'), datetime('now','localtime'), ?)
ON CONFLICT(telegram_id)
DO UPDATE SET checks = checks + 1, username = excluded.username, 
              last_seen = datetime('now','localtime'), last_command = excluded.last_command
```

2. **Statistiche Utente**
```sql
SELECT * FROM users WHERE telegram_id = ?
```

3. **Conteggio Totale Utenti**
```sql
SELECT COUNT(*) AS totale FROM users
```

4. **Log Eventi di Sicurezza**
```sql
INSERT INTO events (telegram_id, event_type, input_value, status_code, is_safe, risk_reason)
VALUES (?, ?, ?, ?, ?, ?)
```

## 🏗️ Architettura del Progetto

```
org.scuola.bot/
├── model/
|   └── Main.java                    # Entry point dell'applicazione
|   ├── CyberBot.java               # Gestione comandi e logica principale
|   ├── MyConfiguration.java        # Caricamento configurazione
├── db/
│   └── DatabaseManager.java    # Gestione database SQLite
└── api/
    ├── CheckIpService.java     # Servizio verifica IP
    ├── UrlScanService.java     # Servizio scansione URL
    └── FileCheckService.java   # Servizio controllo file
```

### Codici di Stato HTTP Utilizzati

Il bot utilizza codici HTTP semantici con immagini associate (tramite http.dog):

- **200**: Risorsa sicura ✅
- **400**: Input non valido ❌
- **404**: File/risorsa sconosciuta ❓
- **451**: Contenuto pericoloso (censura per motivi legali) 🚫

## 🛡️ Note sulla Sicurezza

- Le API key sono gestite tramite file di configurazione esterno
- Il database è locale e non espone dati sensibili
- Il bot non carica mai file, utilizza solo hash SHA-256
- Tutti gli input vengono validati prima dell'analisi
- I risultati si basano esclusivamente sui dati di VirusTotal

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

## 👨‍💻 Autore

Alessia Librelotto
