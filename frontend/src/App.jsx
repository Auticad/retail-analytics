import { useState } from 'react'
import { DEMO } from './api/client'
import DemoBanner from './components/DemoBanner'
import DashboardPage from './pages/DashboardPage'
import StoresPage from './pages/StoresPage'
import './App.css'

const PAGINE = { dashboard: 'Dashboard', negozi: 'Negozi' }

export default function App() {
  const [pagina, setPagina] = useState('dashboard')

  return (
    <>
      <header className="barra">
        <strong>Retail Analytics</strong>
        <nav>
          {Object.entries(PAGINE).map(([chiave, etichetta]) => (
            <button
              key={chiave}
              className={pagina === chiave ? 'attiva' : ''}
              onClick={() => setPagina(chiave)}
            >
              {etichetta}
            </button>
          ))}
        </nav>
      </header>

      {DEMO && <DemoBanner />}

      <main className="contenitore">
        {pagina === 'dashboard' ? <DashboardPage /> : <StoresPage />}
      </main>
    </>
  )
}
