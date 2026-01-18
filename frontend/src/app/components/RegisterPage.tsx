import { useState } from 'react';
import { Mail, Lock, User, Loader2, GraduationCap } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { register } from '../../services/authService';

interface RegisterPageProps {
    onNavigate: (page: string) => void;
}

export function RegisterPage({ onNavigate }: RegisterPageProps) {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [loading, setLoading] = useState(false);

    // @ts-ignore
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            alert("Parollar mos kelmadi!");
            return;
        }

        try {
            setLoading(true);
            await register({
                fullName: formData.fullName,
                email: formData.email,
                password: formData.password
            });
            alert("Ro'yxatdan o'tdingiz! Endi tizimga kiring.");
            onNavigate('login');
        } catch (error) {
            console.error(error);
            alert("Ro'yxatdan o'tishda xatolik yuz berdi.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
            <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-xl">
                <div className="text-center">
                    <div className="mx-auto h-12 w-12 rounded-xl bg-[#0F3460] flex items-center justify-center">
                        <GraduationCap className="h-8 w-8 text-white" />
                    </div>
                    <h2 className="mt-6 text-3xl font-extrabold text-[#0F3460]">Hisob yaratish</h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Hisobingiz bormi?{' '}
                        <button onClick={() => onNavigate('login')} className="font-medium text-[#FF6B35] hover:underline">
                            Kirish
                        </button>
                    </p>
                </div>

                <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
                    <div className="relative">
                        <User className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                        <Input
                            type="text"
                            required
                            placeholder="To'liq ismingiz"
                            className="pl-10"
                            value={formData.fullName}
                            onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                        />
                    </div>
                    <div className="relative">
                        <Mail className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                        <Input
                            type="email"
                            required
                            placeholder="Email manzilingiz"
                            className="pl-10"
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                        />
                    </div>
                    <div className="relative">
                        <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                        <Input
                            type="password"
                            required
                            placeholder="Parol"
                            className="pl-10"
                            value={formData.password}
                            onChange={(e) => setFormData({...formData, password: e.target.value})}
                        />
                    </div>
                    <div className="relative">
                        <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                        <Input
                            type="password"
                            required
                            placeholder="Parolni tasdiqlang"
                            className="pl-10"
                            value={formData.confirmPassword}
                            onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                        />
                    </div>

                    <Button type="submit" className="w-full bg-[#FF6B35] hover:bg-[#E55A2B] h-11" disabled={loading}>
                        {loading ? <Loader2 className="animate-spin mr-2" /> : "Ro'yxatdan o'tish"}
                    </Button>
                </form>
            </div>
        </div>
    );
}