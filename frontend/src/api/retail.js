import { request } from './client'

// Anagrafiche
export const getDistricts = () => request('/districts')

// Negozi: CRUD
export const getStores = (districtId) => request('/stores', { params: { districtId } })
export const createStore = (store) => request('/stores', { method: 'POST', body: store })
export const updateStore = (id, store) => request(`/stores/${id}`, { method: 'PUT', body: store })
export const deleteStore = (id) => request(`/stores/${id}`, { method: 'DELETE' })

// KPI: periodo = { from, to } in formato yyyy-MM-dd; vuoti = ultimi 12 mesi con dati
export const getKpiSummary = (periodo) => request('/kpi/summary', { params: periodo })
export const getKpiMonthly = (periodo) => request('/kpi/monthly', { params: periodo })
export const getKpiStores = (periodo) => request('/kpi/stores', { params: periodo })
