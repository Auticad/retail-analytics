const euro0 = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 })
const euro2 = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' })
const intero = new Intl.NumberFormat('it-IT')
const unDecimale = new Intl.NumberFormat('it-IT', { minimumFractionDigits: 1, maximumFractionDigits: 1 })

export const formatEuro = (valore, decimali = false) => (decimali ? euro2 : euro0).format(valore)
export const formatNumero = (valore) => intero.format(valore)
export const formatPercent = (valore) => `${unDecimale.format(valore)} %`

// '2026-10-31' -> '31/10/2026' senza passare da Date (evita problemi di fuso orario)
export const formatData = (iso) => iso.split('-').reverse().join('/')
