import {
  Bar,
  CartesianGrid,
  ComposedChart,
  Legend,
  Line,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { formatEuro } from '../utils/format'

const inMigliaia = (valore) => `${Math.round(valore / 1000)}k`

export default function MonthlyChart({ dati }) {
  if (dati.length === 0) return <p>Nessuna vendita nel periodo.</p>

  return (
    <div className="grafico">
      <ResponsiveContainer width="100%" height={320}>
        <ComposedChart data={dati} margin={{ top: 10, right: 16, bottom: 0, left: 0 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="month" />
          <YAxis tickFormatter={inMigliaia} />
          <Tooltip formatter={(valore) => formatEuro(valore)} />
          <Legend />
          <Bar dataKey="revenue" name="Fatturato" fill="#e6a817" />
          <Line dataKey="target" name="Obiettivo" stroke="#1565c0" strokeWidth={2} dot={false} />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  )
}
