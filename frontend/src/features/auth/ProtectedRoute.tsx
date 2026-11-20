import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { Role } from '@/types';

export const ProtectedRoute: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
};

export const RoleGuard: React.FC<{ allowedRoles: Role[]; children: React.ReactNode }> = ({
  allowedRoles,
  children,
}) => {
  const { hasRole } = useAuth();

  if (!hasRole(allowedRoles)) {
    return (
      <div className="p-8 bg-red-50 border border-red-200 rounded-lg text-red-800">
        <h3 className="font-semibold text-lg mb-1">Access Restricted</h3>
        <p className="text-sm">You do not have the required permissions to view this resource.</p>
      </div>
    );
  }

  return <>{children}</>;
};
