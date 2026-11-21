import { formatData, formatNumero } from '../utils/format'

export default function StoreTable({ negozi, onModifica, onElimina }) {
  if (negozi.length === 0) return <p>Nessun negozio per il filtro scelto.</p>

  return (
    <table>
      <thead>
        <tr>
          <th>Codice</th>
          <th>Nome</th>
          <th>Città</th>
          <th>Distretto</th>
          <th className="num">m²</th>
          <th>Apertura</th>
          <th>Stato</th>
          <th>Azioni</th>
        </tr>
      </thead>
      <tbody>
        {negozi.map((n) => (
          <tr key={n.id}>
            <td>{n.code}</td>
            <td>{n.name}</td>
            <td>
              {n.city} ({n.province})
            </td>
            <td>{n.districtName}</td>
            <td className="num">{formatNumero(n.sqm)}</td>
            <td>{formatData(n.openedOn)}</td>
            <td>{n.active ? 'Attivo' : 'Non attivo'}</td>
            <td className="azioni-riga">
              <button onClick={() => onModifica(n)}>Modifica</button>
              <button onClick={() => onElimina(n)}>Elimina</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
