export default function KpiCard({ titolo, valore, nota }) {
  return (
    <div className="kpi">
      <span className="kpi-titolo">{titolo}</span>
      <span className="kpi-valore">{valore}</span>
      {nota && <span className="kpi-nota">{nota}</span>}
    </div>
  )
}
