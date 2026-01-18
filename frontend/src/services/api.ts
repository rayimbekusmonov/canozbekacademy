// src/services/api.ts
// @ts-ignore
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config: { headers: { Authorization: string; }; }) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: any) => {
        // @ts-ignore
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response: any) => response,
    (error: { response: { status: number; }; }) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('accessToken');
        }
        // @ts-ignore
        return Promise.reject(error);
    }
);

export default api;