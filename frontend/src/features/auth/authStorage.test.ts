import { describe, it, expect, beforeEach } from 'vitest';
import { authStorage } from '@/lib/auth';
import { User } from '@/types';

// Mock localStorage for node test runner
const storageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString();
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(globalThis, 'localStorage', {
  value: storageMock,
  writable: true,
});

describe('authStorage', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  const demoUser: User = {
    id: 1,
    email: 'admin@procura.local',
    fullName: 'System Administrator',
    role: 'SUPER_ADMIN',
  };

  it('stores and retrieves token, refresh token and user', () => {
    authStorage.setAuth('mock-access-token', 'mock-refresh-token', demoUser);

    expect(authStorage.getToken()).toBe('mock-access-token');
    expect(authStorage.getRefreshToken()).toBe('mock-refresh-token');
    expect(authStorage.getUser()).toEqual(demoUser);
  });

  it('clears all auth credentials on clearAuth', () => {
    authStorage.setAuth('mock-access-token', 'mock-refresh-token', demoUser);
    authStorage.clearAuth();

    expect(authStorage.getToken()).toBeNull();
    expect(authStorage.getRefreshToken()).toBeNull();
    expect(authStorage.getUser()).toBeNull();
  });
});
