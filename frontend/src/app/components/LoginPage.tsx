// src/components/LoginPage.tsx
import { useState } from 'react';
import { Mail, Lock, Loader2, GraduationCap } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
// @ts-ignore
import { login } from '../../services/authService';
interface LoginPageProps {
    onNavigate: (page: string) => void;
}

export function LoginPage({ onNavigate }: LoginPageProps) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);

    // @ts-ignore
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setLoading(true);
            await login({ email, password });
            onNavigate('home');
        } catch (error) {
            alert("Email yoki parol xato!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
            <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-2xl shadow-xl">
                <div className="text-center">
                    <div className="mx-auto h-12 w-12 rounded-xl bg-[#0F3460] flex items-center justify-center">
                        <GraduationCap className="h-8 w-8 text-white" />
                    </div>
                    <h2 className="mt-6 text-3xl font-extrabold text-[#0F3460]">Tizimga kirish</h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Hali ro'yxatdan o'tmaganmisiz?{' '}
                        <button onClick={() => onNavigate('register')} className="font-medium text-[#FF6B35] hover:underline">
                            Hisob yaratish
                        </button>
                    </p>
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="space-y-4">
                        <div className="relative">
                            <Mail className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                            <Input
                                type="email"
                                required
                                className="pl-10"
                                placeholder="Email manzilingiz"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                        <div className="relative">
                            <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                            <Input
                                type="password"
                                required
                                className="pl-10"
                                placeholder="Parolingiz"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                    </div>
                    <Button type="submit" className="w-full bg-[#FF6B35] hover:bg-[#E55A2B]" disabled={loading}>
                        {loading ? <Loader2 className="animate-spin mr-2" /> : "Kirish"}
                    </Button>
                </form>
            </div>
        </div>
    );
}