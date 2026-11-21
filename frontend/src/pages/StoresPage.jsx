import { useEffect, useState } from 'react'
import { createStore, deleteStore, getDistricts, getStores, updateStore } from '../api/retail'
import StoreForm from '../components/StoreForm'
import StoreTable from '../components/StoreTable'

export default function StoresPage() {
  const [negozi, setNegozi] = useState([])
  const [distretti, setDistretti] = useState([])
  const [filtroDistretto, setFiltroDistretto] = useState('')
  const [inModifica, setInModifica] = useState(null) // null = form in modalità "nuovo"
  const [versione, setVersione] = useState(0) // incrementata dopo ogni salvataggio
  const [caricamento, setCaricamento] = useState(true)
  const [errore, setErrore] = useState(null)

  // Distretti: servono al filtro e alla select del form, si caricano una volta
  useEffect(() => {
    getDistricts()
      .then(setDistretti)
      .catch((e) => setErrore(e.message))
  }, [])

  // Negozi: ricaricati quando cambia il filtro o dopo un salvataggio
  useEffect(() => {
    let annullato = false

    getStores(filtroDistretto)
      .then((dati) => {
        if (!annullato) setNegozi(dati)
      })
      .catch((e) => {
        if (!annullato) setErrore(e.message)
      })
      .finally(() => {
        if (!annullato) setCaricamento(false)
      })

    return () => {
      annullato = true
    }
  }, [filtroDistretto, versione])

  // CREATE e UPDATE. Gli errori NON vengono catturati qui: li gestisce il form,
  // che sa mostrare il messaggio sotto il campo giusto.
  async function handleSalva(dati) {
    setErrore(null)
    if (inModifica) {
      await updateStore(inModifica.id, dati)
      setInModifica(null)
    } else {
      await createStore(dati)
    }
    setCaricamento(true)
    setVersione((v) => v + 1)
  }

  function cambiaFiltro(evento) {
    setCaricamento(true)
    setFiltroDistretto(evento.target.value)
  }

  // DELETE. Un negozio con vendite restituisce 409: il messaggio del server spiega perché.
  async function handleElimina(negozio) {
    if (!window.confirm(`Eliminare il negozio ${negozio.code}?`)) return
    setErrore(null)
    try {
      await deleteStore(negozio.id)
      setNegozi((prev) => prev.filter((n) => n.id !== negozio.id))
    } catch (e) {
      setErrore(e.message)
    }
  }

  return (
    <section>
      <h1>Negozi</h1>

      {errore && <p className="errore">{errore}</p>}

      <StoreForm
        key={inModifica?.id ?? 'nuovo'}
        iniziale={inModifica}
        distretti={distretti}
        onSalva={handleSalva}
        onAnnulla={() => setInModifica(null)}
      />

      <div className="filtro">
        <label>
          Distretto
          <select value={filtroDistretto} onChange={cambiaFiltro}>
            <option value="">Tutti</option>
            {distretti.map((d) => (
              <option key={d.id} value={d.id}>
                {d.name}
              </option>
            ))}
          </select>
        </label>
      </div>

      {caricamento ? (
        <p>Caricamento in corso...</p>
      ) : (
        <StoreTable negozi={negozi} onModifica={setInModifica} onElimina={handleElimina} />
      )}
    </section>
  )
}
