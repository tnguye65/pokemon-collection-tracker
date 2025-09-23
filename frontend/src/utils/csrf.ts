// src/utils/csrf.ts
export const getCsrfToken = async (): Promise<string> => {
  const response = await fetch('http://localhost:8080/api/auth/csrf-token', {
    credentials: 'include'
  })
  
  if (!response.ok) {
    throw new Error('Failed to fetch CSRF token')
  }
  
  const data = await response.json()
  return data.token
}