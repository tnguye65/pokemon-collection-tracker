import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../features/auth/hooks/useAuth';

export const Navigation: React.FC = () => {
    const { user, logout } = useAuth();
    const location = useLocation();

    if (!user) {
        return null;    // Don't show navigation on login/register pages
    }

    const isActive = (path: string) => location.pathname === path;

    return (
        <nav className="bg-white shadow-sm border-b">
            <div className="container mx-auto px-4">
                <div className="flex justify-between items-center h-16">
                    <div className="flex items-center space-x-8">
                        <Link to="/" className="text-xl font-bold text-blue-600">
                            PokéCollection
                        </Link>
                    </div>

                    <div className="flex space-x-4">
                        <Link
                            to="/collection"
                            className={`px-3 py-2 rounded-md text-sm font-medium ${
                                isActive('/collection')
                                    ? 'bg-blue-100 text-blue-700'
                                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                            }`}    
                        >
                            My Collection
                        </Link>
                        <Link
                            to="/search"
                            className={`px-3 py-2 rounded-md text-sm font-medium ${
                                isActive('/search')
                                ? 'bg-blue-100 text-blue-700'
                                : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                            }`}
                        >
                            Search Cards
                        </Link>
                        <button
                            onClick={logout}
                            className="text-sm text-red-700 bg-red-100 hover:bg-red-200 px-3 py-2 rounded-md"
                        >
                            Logout
                        </button>
                    </div>
                </div>
            </div>
        </nav>
    )
}