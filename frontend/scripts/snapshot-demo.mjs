// Crea l'istantanea dei dati per la demo statica (GitHub Pages).
// Uso, con il backend retail-analytics avviato:   npm run demo:snapshot
// Scrive i file in public/demo/: vanno salvati su Git insieme al codice.
import { mkdir, rm, writeFile } from 'node:fs/promises'
import { PERIODI_DEMO, demoKey, parametriPieni } from '../src/api/demo.js'

const API = process.env.API_URL ?? 'http://localhost:8081/api/v1'
const CARTELLA = new URL('../public/demo/', import.meta.url)

async function salva(path, params) {
  const pieni = parametriPieni(params)
  const query = pieni.length ? `?${new URLSearchParams(pieni)}` : ''
  const risposta = await fetch(`${API}${path}${query}`, { headers: { Accept: 'application/json' } })
  if (!risposta.ok) throw new Error(`HTTP ${risposta.status} su ${path}${query}`)
  const dati = await risposta.json()
  const nome = `${demoKey(path, params)}.json`
  await writeFile(new URL(nome, CARTELLA), JSON.stringify(dati, null, 2) + '\n')
  console.log(`  ${nome}`)
  return dati
}

try {
  await rm(CARTELLA, { recursive: true, force: true })
  await mkdir(CARTELLA, { recursive: true })
  console.log(`Istantanea da ${API}`)

  const distretti = await salva('/districts')
  await salva('/stores')
  for (const d of distretti) await salva('/stores', { districtId: d.id })

  for (const p of PERIODI_DEMO) {
    const periodo = { from: p.from, to: p.to }
    await salva('/kpi/summary', periodo)
    await salva('/kpi/monthly', periodo)
    await salva('/kpi/stores', periodo)
  }

  await writeFile(new URL('meta.json', CARTELLA), JSON.stringify({ generatedAt: new Date().toISOString() }, null, 2) + '\n')
  console.log('Istantanea completata.')
} catch (errore) {
  console.error(`Istantanea non riuscita: ${errore.message}`)
  console.error('Il backend è avviato? Indirizzo usato: ' + API)
  process.exitCode = 1
}
