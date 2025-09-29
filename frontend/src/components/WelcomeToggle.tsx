// src/components/WelcomeToggle.tsx
import { useState } from 'react'
import LoginForm from './forms/LoginForm'
import RegisterForm from './forms/RegisterForm'

function WelcomeToggle() {
  const [showRegister, setShowRegister] = useState(false)

  return (
    <div className="max-w-4xl mx-auto p-6 flex flex-col items-center">
      <div className="w-full max-w-md">
        {showRegister ? (
          <RegisterForm onSuccessfulRegister={() => setShowRegister(false)} />
        ) : (
          <LoginForm />
        )}

        <div className="text-center mt-4">
          <button
            onClick={() => setShowRegister(!showRegister)}
            className="text-blue-500 hover:text-blue-700 underline"
          >
            {showRegister 
              ? 'Already have an account? Sign in' 
              : "Don't have an account? Sign up"
            }
          </button>
        </div>
      </div>
    </div>
  )
}

export default WelcomeToggle