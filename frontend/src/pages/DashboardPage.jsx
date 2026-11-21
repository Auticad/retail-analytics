import { useEffect, useState } from 'react'
import { getKpiMonthly, getKpiStores, getKpiSummary } from '../api/retail'
import KpiCard from '../components/KpiCard'
import MonthlyChart from '../components/MonthlyChart'
import PeriodFilter from '../components/PeriodFilter'
import StoreRanking from '../components/StoreRanking'
import { formatData, formatEuro, formatNumero, formatPercent } from '../utils/format'

export default function DashboardPage() {
  const [periodo, setPeriodo] = useState({ from: '', to: '' })
  const [dati, setDati] = useState(null)
  const [caricamento, setCaricamento] = useState(true)
  const [errore, setErrore] = useState(null)

  // Il cambio di periodo nasce da un evento: lì impostiamo anche lo stato di caricamento
  function applicaPeriodo(nuovo) {
    setCaricamento(true)
    setErrore(null)
    setPeriodo(nuovo)
  }

  // Si riesegue ogni volta che cambia il periodo
  useEffect(() => {
    let annullato = false

    Promise.all([getKpiSummary(periodo), getKpiMonthly(periodo), getKpiStores(periodo)])
      .then(([summary, monthly, stores]) => {
        if (!annullato) setDati({ summary, monthly, stores })
      })
      .catch((e) => {
        if (!annullato) setErrore(e.message)
      })
      .finally(() => {
        if (!annullato) setCaricamento(false)
      })

    // cleanup: se il periodo cambia prima della risposta, la risposta vecchia viene ignorata
    return () => {
      annullato = true
    }
  }, [periodo])

  return (
    <section>
      <h1>Dashboard vendite</h1>
      <PeriodFilter onApplica={applicaPeriodo} />

      {errore && <p className="errore">{errore}</p>}
      {caricamento && <p>Caricamento in corso...</p>}

      {dati && !caricamento && (
        <>
          <p className="periodo">
            Periodo analizzato: {formatData(dati.summary.from)} – {formatData(dati.summary.to)}
          </p>

          <div className="kpi-griglia">
            <KpiCard titolo="Fatturato" valore={formatEuro(dati.summary.revenue)} />
            <KpiCard
              titolo="Margine"
              valore={formatEuro(dati.summary.margin)}
              nota={`${formatPercent(dati.summary.marginPct)} del fatturato`}
            />
            <KpiCard titolo="Pezzi venduti" valore={formatNumero(dati.summary.units)} />
            <KpiCard
              titolo="Valore medio riga"
              valore={formatEuro(dati.summary.averageRowValue, true)}
              nota={`${formatNumero(dati.summary.saleRows)} righe di vendita`}
            />
          </div>

          <h2>Fatturato mensile e obiettivo</h2>
          <MonthlyChart dati={dati.monthly} />

          <h2>Classifica dei negozi</h2>
          <StoreRanking negozi={dati.stores} />
        </>
      )}
    </section>
  )
}
