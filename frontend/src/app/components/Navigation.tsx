import { Home, BookOpen, GraduationCap, User, LogOut } from 'lucide-react';
import { Button } from './ui/button';
import { logout } from '../../services/authService';

interface NavigationProps {
    currentPage: string;
    onNavigate: (page: string) => void;
}

export function Navigation({ currentPage, onNavigate }: NavigationProps) {
    const userJson = localStorage.getItem('user');
    const user = userJson ? JSON.parse(userJson) : null;

    const handleLogout = () => {
        logout();
        onNavigate('home');
    };

    return (
        <>
            {/* Desktop Navigation */}
            <nav className="hidden md:block bg-white border-b sticky top-0 z-50 shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">

                        {/* 1. Logo */}
                        <div
                            className="flex items-center gap-2 cursor-pointer shrink-0"
                            onClick={() => onNavigate('home')}
                        >
                            <div className="w-10 h-10 rounded-lg flex items-center justify-center bg-[#0F3460]">
                                <GraduationCap className="w-6 h-6 text-white" />
                            </div>
                            <h1 className="text-xl font-bold text-[#0F3460]">canozbek.uz</h1>
                        </div>

                        {/* 2. Center Links */}
                        <div className="flex items-center gap-1">
                            <Button
                                variant={currentPage === 'home' ? 'default' : 'ghost'}
                                onClick={() => onNavigate('home')}
                                className={currentPage === 'home' ? 'bg-[#0F3460]' : ''}
                            >
                                <Home className="w-4 h-4 mr-2" /> Home
                            </Button>
                            <Button
                                variant={currentPage === 'catalog' ? 'default' : 'ghost'}
                                onClick={() => onNavigate('catalog')}
                                className={currentPage === 'catalog' ? 'bg-[#0F3460]' : ''}
                            >
                                <BookOpen className="w-4 h-4 mr-2" /> Courses
                            </Button>
                            <Button
                                variant={currentPage === 'dashboard' ? 'default' : 'ghost'}
                                onClick={() => onNavigate('dashboard')}
                                className={currentPage === 'dashboard' ? 'bg-[#0F3460]' : ''}
                            >
                                <GraduationCap className="w-4 h-4 mr-2" /> My Learning
                            </Button>
                        </div>

                        {/* 3. Right Side: Auth Logic */}
                        <div className="flex items-center gap-3">
                            {user ? (
                                <div className="flex items-center gap-4">
                                    <div className="flex flex-col items-end">
                                        <span className="text-sm font-bold text-[#0F3460] leading-none">{user.fullName}</span>
                                        <span className="text-[10px] text-gray-500 uppercase">{user.role}</span>
                                    </div>
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={handleLogout}
                                        className="border-red-200 text-red-600 hover:bg-red-50"
                                    >
                                        <LogOut className="w-4 h-4 mr-2" /> Chiqish
                                    </Button>
                                </div>
                            ) : (
                                <div className="flex items-center gap-2">
                                    <Button
                                        variant="ghost"
                                        onClick={() => onNavigate('login')}
                                    >
                                        Kirish
                                    </Button>
                                    <Button
                                        className="bg-[#FF6B35] hover:bg-[#E55A2B] text-white"
                                        onClick={() => onNavigate('register')}
                                    >
                                        Ro'yxatdan o'tish
                                    </Button>
                                </div>
                            )}
                        </div>

                    </div>
                </div>
            </nav>

            {/* Mobile Navigation - Top Bar */}
            <nav className="md:hidden bg-white border-b sticky top-0 z-50 shadow-sm px-4">
                <div className="flex items-center justify-between h-14">
                    <div className="flex items-center gap-2" onClick={() => onNavigate('home')}>
                        <div className="w-8 h-8 rounded-lg flex items-center justify-center bg-[#0F3460]">
                            <GraduationCap className="w-5 h-5 text-white" />
                        </div>
                        <h1 className="text-lg font-bold text-[#0F3460]">canozbek.uz</h1>
                    </div>
                    {user && <span className="text-xs font-medium text-gray-500">{user.fullName.split(' ')[0]}</span>}
                </div>
            </nav>

            <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t z-50 shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
                <div className="grid grid-cols-4 h-16">
                    <NavButton active={currentPage === 'home'} onClick={() => onNavigate('home')} icon={Home} label="Home" />
                    <NavButton active={currentPage === 'catalog'} onClick={() => onNavigate('catalog')} icon={BookOpen} label="Courses" />
                    <NavButton active={currentPage === 'dashboard'} onClick={() => onNavigate('dashboard')} icon={GraduationCap} label="Learning" />
                    <NavButton
                        active={currentPage === 'login' || currentPage === 'register'}
                        onClick={() => user ? handleLogout() : onNavigate('login')}
                        icon={user ? LogOut : User}
                        label={user ? "Logout" : "Profile"}
                    />
                </div>
            </nav>
        </>
    );
}

function NavButton({ active, onClick, icon: Icon, label }: any) {
    return (
        <button
            onClick={onClick}
            className={`flex flex-col items-center justify-center gap-1 transition-colors ${
                active ? 'text-[#FF6B35]' : 'text-gray-500'
            }`}
        >
            <Icon className="w-5 h-5" />
            <span className="text-[10px] font-medium">{label}</span>
        </button>
    );
}