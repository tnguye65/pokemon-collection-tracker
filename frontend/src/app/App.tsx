import React from 'react'
import { AuthProvider } from '../features/auth/contexts/AuthContext'
import { useAuth } from '../features/auth/hooks/useAuth'
import PokemonSearch from '../features/pokemon/components/PokemonSearch'
import UserProfile from '../components/forms/UserProfile'
import WelcomeToggle from '../components/WelcomeToggle'

type ActiveTab = 'profile' | 'search';


function AppContent() {
  const { user, isLoading } = useAuth()
  const [activeTab, setActiveTab] = React.useState<ActiveTab>('profile')

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

      {/* Navigation Tabs - Only show when logged in */}
      {user && (
        <nav className="bg-white shadow-sm border-b">
          <div className="container mx-auto">
            <div className="flex space-x-8">
              <button
                onClick={() => setActiveTab('profile')}
                className={`py-4 px-2 border-b-2 font-medium text-sm ${
                  activeTab === 'profile'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Profile
              </button>
              <button
                onClick={() => setActiveTab('search')}
                className={`py-4 px-2 border-b-2 font-medium text-sm ${
                  activeTab === 'search'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Search Cards
              </button>
            </div>
          </div>
        </nav>
      )}
      
      <main className="container mx-auto p-4">
        {user ? (
          <>
            {activeTab === 'profile' && <UserProfile />}
            {activeTab === 'search' && <PokemonSearch />}
          </>
        ) : (
          <WelcomeToggle />
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