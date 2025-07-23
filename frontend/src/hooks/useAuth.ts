// src/hooks/useAuth.ts
import { useState, useEffect, useCallback } from 'react';

interface UserInfo {
  sub: string;
  displayName?: string;
  roles: string[];
}

export function useAuth() {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);

  // Giải mã token
  const parseJwt = (token: string): UserInfo | null => {
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded;
    } catch {
      return null;
    }
  };

  // Kiểm tra khi mount
  useEffect(() => {
    const raw = sessionStorage.getItem('jwtToken') ?? '';
    const token = raw.startsWith('Bearer ') ? raw.slice(7) : raw;
    const info = token ? parseJwt(token) : null;
    setUser(info);
    setLoading(false);
  }, []);

  const login = useCallback((token: string) => {
    sessionStorage.setItem('jwtToken', token);
    setUser(parseJwt(token));
  }, []);

  const logout = useCallback(() => {
    sessionStorage.removeItem('jwtToken');
    setUser(null);
  }, []);

  return {
    user,                     // thông tin user hoặc null
    roles: user?.roles ?? [], // mảng roles
    isAuthenticated: !!user,  // true nếu đã login
    loading,                  // true khi đang kiểm tra token
    login,
    logout,
  };
}
