import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, Role } from '@/types';
import { authStorage } from '@/lib/auth';
import { apiClient } from '@/lib/api';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<void>;
  logout: () => void;
  hasRole: (roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const stored = authStorage.getUser();
    const token = authStorage.getToken();
    if (stored && token) {
      setUser(stored);
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, pass: string) => {
    const res = await apiClient<{ data: { accessToken: string; refreshToken: string; user: User } }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password: pass }),
    });

    const { accessToken, refreshToken, user: loggedUser } = res.data;
    authStorage.setAuth(accessToken, refreshToken, loggedUser);
    setUser(loggedUser);
  };

  const logout = () => {
    const refresh = authStorage.getRefreshToken();
    if (refresh) {
      apiClient('/auth/logout', {
        method: 'POST',
        body: JSON.stringify({ refreshToken: refresh }),
      }).catch(() => {});
    }
    authStorage.clearAuth();
    setUser(null);
  };

  const hasRole = (roles: Role[]): boolean => {
    if (!user) return false;
    return roles.includes(user.role);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
