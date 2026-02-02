import { useState, useEffect } from 'react';
import { Navigation } from './components/Navigation';
import { HomePage } from './components/HomePage';
import { CourseCatalog } from './components/CourseCatalog';
import { LearningDashboard } from './components/LearningDashboard';
import { LoginPage } from "./components/LoginPage";
import { RegisterPage } from "./components/RegisterPage";
import {AdminPanel} from "./components/AdminPanel";

// 1. Page tipini yangilash
type Page = 'home' | 'catalog' | 'dashboard' | 'login' | 'register' | 'admin';

export default function App() {
    const [currentPage, setCurrentPage] = useState<Page>('home');
    const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null);

    // Foydalanuvchi rolni tekshirish (JWT ichidan yoki localStorage dan olish)
    // Siz pgAdmin orqali rolni ADMIN qildingiz, endi uni tekshirish kerak
    const isAdmin = () => {
        const userRole = localStorage.getItem('userRole'); // To'g'ridan-to'g'ri string
        const userJson = localStorage.getItem('user'); // Obyekt ko'rinishida
        const user = userJson ? JSON.parse(userJson) : null;

        // userRole stringi ADMIN bo'lsa YOKI user obyekti ichidagi role ADMIN bo'lsa
        return userRole === 'ADMIN' || (user && user.role === 'ADMIN');
    };

    const handleNavigate = (page: string, courseId?: number) => {
        const isAuthenticated = localStorage.getItem('accessToken') !== null;

        // Dashboard himoyasi
        if (page === 'dashboard' && !isAuthenticated) {
            alert("Iltimos, avval tizimga kiring!");
            setCurrentPage('login');
            return;
        }

        // ADMIN sahifasi himoyasi
        if (page === 'admin' && !isAdmin()) {
            alert("Bu sahifaga kirish uchun huquqingiz yo'q!");
            setCurrentPage('home');
            return;
        }

        setCurrentPage(page as Page);
        if (courseId) setSelectedCourseId(courseId);
        window.scrollTo(0, 0);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <Navigation currentPage={currentPage} onNavigate={handleNavigate} />

            <main className="pb-20 md:pb-0">
                {currentPage === 'home' && <HomePage onNavigate={handleNavigate} />}
                {currentPage === 'catalog' && <CourseCatalog onNavigate={handleNavigate} />}

                {/* ADMIN PANELNI SHU YERGA QO'SHAMIZ */}
                {currentPage === 'admin' && <AdminPanel />}

                {currentPage === 'dashboard' && selectedCourseId && (
                    <LearningDashboard courseId={selectedCourseId} />
                )}

                {currentPage === 'login' && <LoginPage onNavigate={handleNavigate} />}
                {currentPage === 'register' && <RegisterPage onNavigate={handleNavigate} />}
            </main>
        </div>
    );
}