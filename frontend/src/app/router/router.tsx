import { createBrowserRouter, Navigate } from 'react-router-dom';
import { App } from '../App';
import { CollectionPage } from '../../features/collection/CollectionPage';
import LoginForm from '../../components/forms/LoginForm';
import RegisterForm from '../../components/forms/RegisterForm';
import UserProfile from '../../components/forms/UserProfile';
import PokemonSearch from '../../features/pokemon/components/PokemonSearch';
import { ProtectedRoute } from './ProtectedRoute';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                index: true,
                element: <Navigate to="/search" replace />,
            },
            {
                path: 'login',
                element: <LoginForm />,
            },
            {
                path: 'register',
                element: <RegisterForm onSuccessfulRegister={() => window.location.href = '/login'} />,
            },
            {
                path: 'collection',
                element: (
                    <ProtectedRoute>
                        <CollectionPage />
                    </ProtectedRoute>
                ),
            },
            {
                path: 'search',
                element: (
                    <ProtectedRoute>
                        <PokemonSearch />
                    </ProtectedRoute>
                ),
            },
            {
                path: 'profile',
                element: (
                    <ProtectedRoute>
                        <UserProfile />
                    </ProtectedRoute>
                ),
            },
        ],
    },
]);