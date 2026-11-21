import { useEffect, useState } from 'react'
import { formatData } from '../utils/format'

// Avviso mostrato solo nella build demo: dati statici, operazioni di scrittura disattivate
export default function DemoBanner() {
  const [dataIstantanea, setDataIstantanea] = useState(null)

  useEffect(() => {
    fetch(`${import.meta.env.BASE_URL}demo/meta.json`)
      .then((r) => (r.ok ? r.json() : null))
      .then((meta) => setDataIstantanea(meta?.generatedAt?.slice(0, 10) ?? null))
      .catch(() => setDataIstantanea(null))
  }, [])

  return (
    <div className="avviso-demo">
      <strong>Demo in sola lettura.</strong> Dati di esempio salvati da retail-analytics
      {dataIstantanea && ` il ${formatData(dataIstantanea)}`}: i periodi sono predefiniti e le
      operazioni di modifica sono disattivate. L'applicazione completa richiede il backend Spring Boot.
    </div>
  )
}
