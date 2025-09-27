import { useState, useEffect, type ReactNode } from 'react'
import { AuthContext, type AuthContextType, type User } from './auth-context'

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  const generateCsrfTokenHeaders = async () : Promise<HeadersInit> => {
    const headers: HeadersInit = {
        'Content-Type': 'application/json'
    };
    try {
      const csrfResponse = await fetch('http://localhost:8080/api/auth/csrf-token', {
        credentials: 'include'
      })

      if (csrfResponse.ok) {
        const csrfData = await csrfResponse.json();
        headers[csrfData.headerName] = csrfData.token;
      }
      
    } catch (error) {
      console.warn('Error generating CSRF token:', error);
    }
    return headers
  };

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
    await generateCsrfTokenHeaders();
    console.log('Login completed, CSRF token should be available');
  }

  const logout = async () : Promise<void> => {
    try {
      const headers = await generateCsrfTokenHeaders();
      const response = await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers,
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

  const refreshCsrfToken = async (): Promise<void> => {
    await generateCsrfTokenHeaders();
  };

  useEffect(() => {
    checkAuthStatus()
  }, [])

  const value: AuthContextType = {
    user,
    isLoading,
    login,
    logout,
    refreshCsrfToken
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}