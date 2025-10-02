import React from 'react';
import { Outlet } from 'react-router-dom';
import { AuthProvider } from '../features/auth/contexts/AuthContext';
import { Navigation } from '../components/layout/Navigation';

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <main className="container mx-auto px-4 py-6">
          <Outlet />
        </main>
      </div>
    </AuthProvider>
  );
};