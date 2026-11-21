import { useState } from 'react'
import { DEMO } from '../api/client'
import { PERIODI_DEMO } from '../api/demo'

// Demo: solo i periodi presenti nell'istantanea dei dati
function FiltroDemo({ onApplica }) {
  const [attivo, setAttivo] = useState(PERIODI_DEMO[0].id)

  function scegli(periodo) {
    setAttivo(periodo.id)
    onApplica({ from: periodo.from, to: periodo.to })
  }

  return (
    <div className="filtro">
      {PERIODI_DEMO.map((p) => (
        <button key={p.id} className={p.id === attivo ? 'attiva' : ''} onClick={() => scegli(p)}>
          {p.etichetta}
        </button>
      ))}
    </div>
  )
}

function FiltroDate({ onApplica }) {
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')

  function handleSubmit(evento) {
    evento.preventDefault()
    onApplica({ from, to })
  }

  function handleReset() {
    setFrom('')
    setTo('')
    onApplica({ from: '', to: '' })
  }

  return (
    <form className="filtro" onSubmit={handleSubmit}>
      <label>
        Dal
        <input type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
      </label>
      <label>
        Al
        <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />
      </label>
      <button type="submit">Applica</button>
      <button type="button" onClick={handleReset}>
        Ultimi 12 mesi
      </button>
    </form>
  )
}

export default function PeriodFilter({ onApplica }) {
  return DEMO ? <FiltroDemo onApplica={onApplica} /> : <FiltroDate onApplica={onApplica} />
}
