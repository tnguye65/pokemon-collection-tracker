//import { useState } from 'react'
import LoginForm from '../components/forms/LoginForm'

function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-blue-600 text-white p-4">
        <h1 className="text-2xl font-bold">Pokemon Collection Tracker</h1>
      </header>
      
      <main className="container mx-auto p-4 mt-8">
        <LoginForm />
      </main>
    </div>
  )
}

export default App