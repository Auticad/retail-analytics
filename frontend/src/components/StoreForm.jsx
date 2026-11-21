import { useState } from 'react'
import { ApiError } from '../api/client'

const VUOTO = {
  code: '',
  name: '',
  city: '',
  province: '',
  districtId: '',
  sqm: '',
  openedOn: '',
  active: true,
}

// I campi del form lavorano con stringhe: convertiamo il negozio ricevuto dal server
function daNegozio(n) {
  return {
    code: n.code,
    name: n.name,
    city: n.city,
    province: n.province,
    districtId: String(n.districtId),
    sqm: String(n.sqm),
    openedOn: n.openedOn,
    active: n.active,
  }
}

// ...e prima dell'invio riconvertiamo nei tipi attesi da StoreRequest
function aRichiesta(v) {
  return {
    ...v,
    districtId: v.districtId === '' ? null : Number(v.districtId),
    sqm: v.sqm === '' ? null : Number(v.sqm),
    openedOn: v.openedOn === '' ? null : v.openedOn,
  }
}

function Campo({ etichetta, errore, children }) {
  return (
    <label className={errore ? 'campo con-errore' : 'campo'}>
      {etichetta}
      {children}
      {errore && <small>{errore}</small>}
    </label>
  )
}

export default function StoreForm({ iniziale, distretti, onSalva, onAnnulla }) {
  const [valori, setValori] = useState(iniziale ? daNegozio(iniziale) : VUOTO)
  const [erroriCampo, setErroriCampo] = useState({})
  const [erroreGenerale, setErroreGenerale] = useState(null)
  const [invio, setInvio] = useState(false)

  // Un solo gestore per tutti i campi: usa l'attributo name
  function handleChange(evento) {
    const { name, value, type, checked } = evento.target
    setValori((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }))
  }

  async function handleSubmit(evento) {
    evento.preventDefault()
    setInvio(true)
    setErroriCampo({})
    setErroreGenerale(null)
    try {
      await onSalva(aRichiesta(valori))
      if (!iniziale) setValori(VUOTO)
    } catch (e) {
      if (e instanceof ApiError && Object.keys(e.fieldErrors).length > 0) {
        setErroriCampo(e.fieldErrors) // 400 di validazione o 409 sul codice
      } else {
        setErroreGenerale(e.message)
      }
    } finally {
      setInvio(false)
    }
  }

  return (
    <form className="scheda" onSubmit={handleSubmit} noValidate>
      <h2>{iniziale ? `Modifica negozio ${iniziale.code}` : 'Nuovo negozio'}</h2>
      {erroreGenerale && <p className="errore">{erroreGenerale}</p>}

      <div className="campi">
        <Campo etichetta="Codice" errore={erroriCampo.code}>
          <input name="code" value={valori.code} onChange={handleChange} maxLength={10} />
        </Campo>
        <Campo etichetta="Nome" errore={erroriCampo.name}>
          <input name="name" value={valori.name} onChange={handleChange} />
        </Campo>
        <Campo etichetta="Città" errore={erroriCampo.city}>
          <input name="city" value={valori.city} onChange={handleChange} />
        </Campo>
        <Campo etichetta="Provincia" errore={erroriCampo.province}>
          <input name="province" value={valori.province} onChange={handleChange} />
        </Campo>
        <Campo etichetta="Distretto" errore={erroriCampo.districtId}>
          <select name="districtId" value={valori.districtId} onChange={handleChange}>
            <option value="">Seleziona...</option>
            {distretti.map((d) => (
              <option key={d.id} value={d.id}>
                {d.name}
              </option>
            ))}
          </select>
        </Campo>
        <Campo etichetta="Superficie (m²)" errore={erroriCampo.sqm}>
          <input name="sqm" type="number" value={valori.sqm} onChange={handleChange} />
        </Campo>
        <Campo etichetta="Data di apertura" errore={erroriCampo.openedOn}>
          <input name="openedOn" type="date" value={valori.openedOn} onChange={handleChange} />
        </Campo>
        <label className="campo spunta">
          <input name="active" type="checkbox" checked={valori.active} onChange={handleChange} />
          Attivo
        </label>
      </div>

      <div className="azioni">
        <button type="submit" disabled={invio}>
          {invio ? 'Salvataggio...' : iniziale ? 'Salva modifiche' : 'Crea negozio'}
        </button>
        {iniziale && (
          <button type="button" onClick={onAnnulla}>
            Annulla
          </button>
        )}
      </div>
    </form>
  )
}
