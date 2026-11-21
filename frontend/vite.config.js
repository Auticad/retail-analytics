import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  plugins: [react()],
  // La build demo è pubblicata su GitHub Pages in https://<utente>.github.io/<repository>/
  base: mode === 'demo' ? (process.env.DEMO_BASE ?? '/retail-analytics/') : '/',
  server: {
    proxy: {
      // le chiamate a /api vanno a Spring Boot (retail-analytics usa la porta 8081)
      '/api': 'http://localhost:8081',
    },
  },
}))
