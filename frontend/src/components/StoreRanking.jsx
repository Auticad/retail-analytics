import { formatEuro, formatPercent } from '../utils/format'

export default function StoreRanking({ negozi }) {
  return (
    <table>
      <thead>
        <tr>
          <th>#</th>
          <th>Negozio</th>
          <th>Distretto</th>
          <th className="num">Fatturato</th>
          <th className="num">Margine %</th>
          <th className="num">Obiettivo %</th>
          <th className="num">€/m²</th>
        </tr>
      </thead>
      <tbody>
        {negozi.map((n, indice) => (
          <tr key={n.storeId}>
            <td>{indice + 1}</td>
            <td>
              {n.storeCode} · {n.storeName}
            </td>
            <td>{n.district}</td>
            <td className="num">{formatEuro(n.revenue)}</td>
            <td className="num">{formatPercent(n.marginPct)}</td>
            <td className={n.targetAttainmentPct < 100 ? 'num sotto' : 'num'}>
              {formatPercent(n.targetAttainmentPct)}
            </td>
            <td className="num">{formatEuro(n.revenuePerSqm)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
