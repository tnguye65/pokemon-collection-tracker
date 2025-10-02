import { useState } from 'react'
import { Link } from 'react-router-dom';

interface RegisterFormProps {
  onSuccessfulRegister: () => void;
}
    
function RegisterForm({ onSuccessfulRegister }: RegisterFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')  // Will add password confirmation later
  const [isLoading, setIsLoading] = useState(false)
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setMessage('')

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
        credentials: 'include'
      })

      const data = await response.json()

      if (response.ok) {
        // Success! This matches your AuthResponse DTO
        setMessage(`Registration successful! Redirecting to login...`)
        alert("Registration successful! Redirecting to login...")
        console.log('Registration Response:', data)
        console.log('Message:', message)
        onSuccessfulRegister()
      } else {
        // Error from your backend
        setMessage(data.message || 'Registration failed')
      }
    } catch (error) {
      setMessage('Network error - is your backend running?')
      console.error('Registration error:', error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">Register</h2>
      
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
            disabled={isLoading}
          />
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
            required
            disabled={isLoading}
          />
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
        >
          {isLoading ? 'Registering...' : 'Register'}
        </button>

        {/* {message && (
          <div className={`mt-4 p-2 rounded ${
            message.includes('successful') ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
          }`}>
            {message}
          </div>
        )} */}
      </form>
      
      <p className="mt-4 text-center text-sm text-gray-600">
            <Link
                to="/login"
                className="font-medium text-blue-600 hover:text-blue-500"
            >
            Already have an account? Login here!
            </Link>
        </p>
    </div>
  )
}

export default RegisterForm