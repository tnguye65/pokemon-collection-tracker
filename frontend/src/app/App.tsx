import { AuthProvider } from '../features/auth/contexts/AuthContext'
import { useAuth } from '../features/auth/hooks/useAuth'
import LoginForm from '../components/forms/LoginForm'
import UserProfile from '../components/forms/UserProfile'

function AppContent() {
  const { user, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4">Loading...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-blue-600 text-white p-4">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">Pokemon Collection Tracker</h1>
          {user && (
            <span className="text-blue-100">Welcome, {user.username}!</span>
          )}
        </div>
      </header>
      
      <main className="container mx-auto p-4">
        {user ? (
          <UserProfile />
        ) : (
          <LoginForm />
        )}
      </main>
    </div>
  )
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

export default App