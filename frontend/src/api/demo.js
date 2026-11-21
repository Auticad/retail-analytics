// Elementi condivisi tra la modalità demo del front-end e lo script che crea l'istantanea dei dati.
// Nessun riferimento a import.meta.env: il file viene importato anche da Node.

// Periodi disponibili nella demo: ogni periodo ha i propri file JSON nell'istantanea.
// Le date seguono i dati di esempio caricati da Flyway (vendite dal 2025-01-01 al 2026-10-31).
export const PERIODI_DEMO = [
  { id: 'ultimi12', etichetta: 'Ultimi 12 mesi', from: '', to: '' },
  { id: 'anno2025', etichetta: 'Anno 2025', from: '2025-01-01', to: '2025-12-31' },
  { id: 'anno2026', etichetta: '2026 (gen–ott)', from: '2026-01-01', to: '2026-10-31' },
]

// Parametri senza valori vuoti, in ordine alfabetico
export function parametriPieni(params) {
  if (!params) return []
  return Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== '')
    .sort(([a], [b]) => a.localeCompare(b))
}

// Nome del file JSON che contiene la risposta di una chiamata GET.
// '/kpi/summary' + { from: '2025-01-01', to: '2025-12-31' } -> 'kpi-summary__from-2025-01-01__to-2025-12-31'
export function demoKey(path, params) {
  const base = path.replace(/^\//, '').replaceAll('/', '-')
  const suffisso = parametriPieni(params)
    .map(([k, v]) => `__${k}-${v}`)
    .join('')
  return base + suffisso
}
