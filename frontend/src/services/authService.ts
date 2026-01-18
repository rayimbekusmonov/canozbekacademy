// src/services/authService.ts
import api from './api';

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    user: {
        id: number;
        email: string;
        fullName: string;
        role: string;
    };
}

export const login = async (credentials: any) => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('user', JSON.stringify(response.data.user));
    }
    return response.data;
};

export const register = async (userData: any) => {
    const response = await api.post<AuthResponse>('/auth/register', userData);
    return response.data;
};

export const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
    window.location.reload();
};