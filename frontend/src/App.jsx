import { useEffect, useState } from 'react'
import axios from 'axios'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE

export default function App() {
  const [status, setStatus] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [updatedAt, setUpdatedAt] = useState('')

  const checkBackend = async () => {
    setLoading(true)
    setError('')
    try {
      const res = await axios.get(`${API_BASE}/api/health`, { responseType: 'text' })
      const text = typeof res.data === 'string' ? res.data : JSON.stringify(res.data)
      setStatus(text)
      setUpdatedAt(new Date().toLocaleTimeString())
    } catch (e) {
      setError(e?.message || 'Request failed')
      setStatus('')
    } finally {
      setLoading(false)
    }
  }

  // Run once when the component mounts
  useEffect(() => {
    checkBackend()
  }, [])

  const isOk = status.trim().toUpperCase() === 'OK'

  return (
    <div style={{ padding: 24, maxWidth: 720, margin: '0 auto' }}>
      <h1>Frontend ⇄ Backend Connection Status</h1>

      <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginTop: 12 }}>
        <span className={`badge ${isOk ? 'ok' : 'bad'}`}>
          {loading ? 'Checking...' : (status || 'Unknown')}
        </span>
        <button onClick={checkBackend} disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh'}
        </button>
      </div>

      {updatedAt && !loading && (
        <p style={{ color: '#888', marginTop: 8 }}>Last update: {updatedAt}</p>
      )}

      {error && (
        <p style={{ color: 'crimson', marginTop: 8 }}>Error: {error}</p>
      )}

      <hr style={{ margin: '24px 0' }} />

      <p>
        Reading backend URL from: <code>.env</code> → <code>VITE_API_BASE={API_BASE}</code>
      </p>
      <p>Backend endpoint: <code>/api/health</code> (Spring Boot returns <code>"OK"</code>)</p>
    </div>
  )
}
