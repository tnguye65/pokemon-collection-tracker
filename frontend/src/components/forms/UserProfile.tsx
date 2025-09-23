import { useState } from 'react'
import { useAuth } from '../../hooks/useAuth'

function UserProfile() {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const { user, logout } = useAuth()

    const handleLogout = async () => {
        setLoading(true)
        try {
            await logout()
        } catch (err) {
            setError('Logout failed')
            console.error('Logout error:', err)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="bg-white p-6 rounded-lg shadow-md max-w-md mx-auto mt-4">
            <h2 className="text-xl font-bold mb-4">User Profile Test</h2>
            
            <div className="space-y-4">

                {user && (
                    <div className="bg-green-50 p-3 rounded">
                        <p><strong>Username:</strong> {user.username}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                    </div>
                )}

                {error && (
                    <div className="bg-red-50 text-red-700 p-3 rounded">
                        {error}
                    </div>
                )}

                <button
                    onClick={handleLogout}
                    className="w-full bg-red-600 text-white p-2 rounded hover:bg-red-700"
                >
                    {loading ? 'Logging out...' : 'Logout'}
                </button>
            </div>
        </div>
    )
}

export default UserProfile