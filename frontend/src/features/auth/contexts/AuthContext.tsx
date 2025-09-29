import { useState, useEffect, type ReactNode } from 'react'
import { AuthContext, type AuthContextType, type User } from './auth-context'

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

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

  const login = async (userData: User) => {
    setUser(userData);
  }

  const logout = async () : Promise<void> => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });

      if (response.ok) {
        setUser(null);
        console.log('Logout successful');
      } else {
        console.error('Logout failed');
      }

    } catch (error) {
      console.error('Logout error:', error)
      setUser(null)
    }
  }

  useEffect(() => {
    checkAuthStatus()
  }, [])

  const value: AuthContextType = {
    user,
    isLoading,
    login,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * How It Works in Practice
App startup: Automatically checks if user is logged in
Any component can use useAuth() to access:
user - current user info or null
isLoading - whether auth check is in progress
login() - function to set user after successful login
logout() - function to log user out
This pattern lets you easily protect routes, show/hide UI elements, and manage authentication state without prop drilling!
 */