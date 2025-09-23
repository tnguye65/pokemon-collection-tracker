import { useState, useEffect, type ReactNode } from 'react'
import { AuthContext, type User } from './auth-context'

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    checkAuthStatus()
  }, [])

  const checkAuthStatus = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/me', {
        credentials: 'include'
      })

      if (response.ok) {
        const userData = await response.json()
        setUser({ username: userData.username, email: userData.email })
      }
    } catch (error) {
      console.log('Not authenticated: ', error)
    } finally {
      setIsLoading(false)
    }
  }

  const login = (userData: User) => {
    setUser(userData)
  }

  const logout = async () => {
    try {
      const csrfResponse = await fetch('http://localhost:8080/api/auth/csrf-token', {
        credentials: 'include'
      })
      const csrfData = await csrfResponse.json()

      await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: {
          'X-XSRF-TOKEN': csrfData.token
        },
        credentials: 'include'
      })

      setUser(null)
    } catch (error) {
      console.error('Logout error:', error)
      setUser(null)
    }
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}