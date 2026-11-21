# Front-end React di retail-analytics

Interfaccia React (Vite + JavaScript) che usa le API REST del progetto: gestione dei negozi e dashboard KPI.
La guida completa è `guide/05-frontend-react.html`.

## Avvio in sviluppo

1. Avvia il backend (porta 8081) come descritto nel README principale.
2. In questa cartella:

   ```powershell
   npm install      # solo la prima volta
   npm run dev      # http://localhost:5173
   ```

Il proxy di Vite (`vite.config.js`) inoltra le chiamate `/api` a `http://localhost:8081`: il backend non richiede configurazione CORS.

## Script

| Comando | Effetto |
|---|---|
| `npm run dev` | server di sviluppo con ricaricamento a caldo |
| `npm run lint` | analisi statica con Oxlint |
| `npm run build` | build ottimizzata in `dist/` |
| `npm run preview` | serve localmente la build |
| `npm run demo:snapshot` | salva in `public/demo/` le risposte delle API per la demo (backend avviato) |
| `npm run build:demo` | build in modalità demo per GitHub Pages, percorso base `/retail-analytics/` |
| `npm run preview:demo` | serve localmente la build demo su `http://localhost:4173/retail-analytics/` |

## Demo statica

In modalità demo (`vite build --mode demo`) il client non chiama le API: legge i file JSON di `public/demo/`,
mostra un avviso in cima alla pagina, offre tre periodi predefiniti nella dashboard e rifiuta le operazioni di
scrittura. La pubblicazione su GitHub Pages è descritta nel README principale.
