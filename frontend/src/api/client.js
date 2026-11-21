import { demoKey, parametriPieni } from './demo'

// Indirizzo base delle API. Con il proxy di Vite basta un percorso relativo.
const BASE_URL = import.meta.env.VITE_API_URL ?? '/api/v1'

// Modalità demo (build per GitHub Pages): i dati arrivano da file JSON statici
export const DEMO = import.meta.env.MODE === 'demo'

// Errore applicativo costruito dalla risposta ProblemDetail (RFC 9457) di Spring
export class ApiError extends Error {
  constructor(status, problem) {
    super(problem?.detail ?? messaggioPerStato(status))
    this.name = 'ApiError'
    this.status = status
    this.title = problem?.title ?? null
    // 400: { campo: messaggio } da ApiExceptionHandler
    this.fieldErrors = { ...(problem?.errors ?? {}) }
    // 409 riferito a un campo (es. codice duplicato)
    if (problem?.field) {
      this.fieldErrors[problem.field] = problem.detail
    }
  }
}

function messaggioPerStato(status) {
  if (status === 0 || status === 502 || status === 503 || status === 504) {
    return 'Backend non raggiungibile: retail-analytics è avviato sulla porta 8081?'
  }
  return `Errore HTTP ${status}`
}

// Trasforma { from: '2026-01-01', to: '' } in "?from=2026-01-01", scartando i valori vuoti
function queryString(params) {
  const pieni = parametriPieni(params)
  return pieni.length ? `?${new URLSearchParams(pieni)}` : ''
}

// Demo: le letture vengono da public/demo/*.json, le scritture non sono disponibili
async function requestDemo(path, method, params) {
  if (method !== 'GET') {
    throw new ApiError(405, {
      detail: 'Demo in sola lettura: per creare, modificare o eliminare avvia il progetto completo.',
    })
  }
  const risposta = await fetch(`${import.meta.env.BASE_URL}demo/${demoKey(path, params)}.json`)
  if (!risposta.ok) {
    throw new ApiError(404, { detail: 'Dati non presenti nella demo per questa selezione.' })
  }
  return risposta.json()
}

export async function request(path, { method = 'GET', body, params } = {}) {
  if (DEMO) return requestDemo(path, method, params)

  const opzioni = { method, headers: { Accept: 'application/json' } }
  if (body !== undefined) {
    opzioni.headers['Content-Type'] = 'application/json'
    opzioni.body = JSON.stringify(body)
  }

  let risposta
  try {
    risposta = await fetch(`${BASE_URL}${path}${queryString(params)}`, opzioni)
  } catch {
    throw new ApiError(0, null) // rete assente o server spento (senza proxy)
  }

  if (risposta.status === 204) return null // DELETE riuscita: nessun corpo

  const tipo = risposta.headers.get('Content-Type') ?? ''
  const dati = tipo.includes('json') ? await risposta.json() : null

  if (!risposta.ok) throw new ApiError(risposta.status, dati)
  return dati
}
