import { useState, useEffect } from 'react';
import { Navigation } from './components/Navigation';
import { HomePage } from './components/HomePage';
import { CourseCatalog } from './components/CourseCatalog';
import { LearningDashboard } from './components/LearningDashboard';
import { LoginPage } from "./components/LoginPage";
import { RegisterPage } from "./components/RegisterPage";

type Page = 'home' | 'catalog' | 'dashboard' | 'login' | 'register';

export default function App() {
    const [currentPage, setCurrentPage] = useState<Page>('home');
    const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null);

    // Foydalanuvchi kirganini tekshirish funksiyasi
    const isAuthenticated = () => {
        return localStorage.getItem('accessToken') !== null;
    };

    const handleNavigate = (page: string, courseId?: number) => {
        // PROTECTED ROUTE LOGIC:
        // Agar foydalanuvchi kirmagan bo'lsa va Dashboardga o'tmoqchi bo'lsa
        if (page === 'dashboard' && !isAuthenticated()) {
            alert("Iltimos, avval tizimga kiring!");
            setCurrentPage('login');
            return;
        }

        setCurrentPage(page as Page);
        if (courseId) {
            setSelectedCourseId(courseId);
        }
        window.scrollTo(0, 0);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navigation har doim ko'rinadi */}
            <Navigation currentPage={currentPage} onNavigate={handleNavigate} />

            <main className="pb-20 md:pb-0">
                {currentPage === 'home' && <HomePage onNavigate={handleNavigate} />}
                {currentPage === 'catalog' && <CourseCatalog onNavigate={handleNavigate} />}

                {currentPage === 'dashboard' && selectedCourseId && (
                    <LearningDashboard courseId={selectedCourseId} />
                )}

                {currentPage === 'login' && <LoginPage onNavigate={handleNavigate} />}
                {currentPage === 'register' && <RegisterPage onNavigate={handleNavigate} />}
            </main>
        </div>
    );
}