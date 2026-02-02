import { Key, useEffect, useState} from 'react';
import {Play, Lock, CheckCircle2, ChevronDown, ChevronUp, Upload, FileText, Loader2, Edit, Trash2} from 'lucide-react';
import {Button} from './ui/button';
import {Badge} from './ui/badge';
import {Textarea} from './ui/textarea';
import {Progress} from './ui/progress';
// @ts-ignore
import { getDashboardData, submitHomework } from '../../services/dashboardService';
import type { DashboardData, Lesson } from '../../services/dashboardService';

interface LearningDashboardProps {
    courseId: number;
}

export function LearningDashboard({courseId}: LearningDashboardProps) {
    const [data, setData] = useState<DashboardData | null>(null);
    const [selectedLesson, setSelectedLesson] = useState<Lesson | null>(null);
    const [homeworkText, setHomeworkText] = useState('');
    const [homeworkSubmitted, setHomeworkSubmitted] = useState(false);
    const [expandedSection, setExpandedSection] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);

    // Rolni tekshirish
    const userRole = localStorage.getItem('userRole');
    const isAdmin = userRole === 'ADMIN';

    useEffect(() => {
        // @ts-ignore
        const fetchDashboard = async () => {
            try {
                setLoading(true);
                const result = await getDashboardData(courseId);
                setData(result);

                if (result.sections.length > 0 && result.sections[0].lessons.length > 0) {
                    setSelectedLesson(result.sections[0].lessons[0]);
                    setExpandedSection(Number(result.sections[0].id));
                }
            } catch (error) {
                console.error("Dashboard ma'lumotlarini yuklashda xato:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchDashboard();
    }, [courseId]);

    const handleLessonClick = (lesson: Lesson) => {
        // Admin uchun darslar doim ochiq
        if (!lesson.locked || isAdmin) {
            setSelectedLesson(lesson);
            setHomeworkSubmitted(false);
            setHomeworkText('');
        }
    };

    // @ts-ignore
    const handleSubmit = async () => {
        if (selectedLesson && homeworkText.trim()) {
            try {
                await submitHomework(selectedLesson.id, homeworkText);
                setHomeworkSubmitted(true);
            } catch (error) {
                alert("Xatolik yuz berdi. Qaytadan urining.");
            }
        }
    };

    if (loading) return (
        <div className="flex h-screen items-center justify-center">
            <Loader2 className="w-12 h-12 animate-spin text-[#FF6B35]"/>
        </div>
    );

    if (!data || !selectedLesson) return <div className="p-20 text-center">Ma'lumot topilmadi.</div>;

    return (
        <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
            <div className="max-w-[1920px] mx-auto">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-0 lg:gap-6 lg:p-6">

                    <div className="lg:col-span-2 bg-white lg:rounded-xl overflow-hidden shadow-lg">
                        {/* Video Player */}
                        <div className="relative bg-black aspect-video">
                            {/* 'includes' o'rniga 'indexOf' ishlatildi (TS2550 xatosi yechimi) */}
                            {selectedLesson.videoUrl.indexOf('localhost:9000') !== -1 ? (
                                <video
                                    key={selectedLesson.videoUrl}
                                    controls
                                    className="w-full h-full"
                                    controlsList="nodownload"
                                >
                                    <source src={selectedLesson.videoUrl} type="video/mp4" />
                                    Brauzeringiz videoni qo'llab-quvvatlamaydi.
                                </video>
                            ) : (
                                <iframe
                                    className="w-full h-full"
                                    src={`https://www.youtube.com/embed/${selectedLesson.videoUrl}`}
                                    title={selectedLesson.title}
                                    allowFullScreen
                                ></iframe>
                            )}
                        </div>

                        {/* Admin Boshqaruv Paneli */}
                        {isAdmin && (
                            <div className="p-4 bg-orange-50 border-b border-orange-200 flex justify-between items-center">
                                <div className="flex items-center gap-2">
                                    <Badge className="bg-[#FF6B35]">ADMIN</Badge>
                                    <span className="text-sm text-gray-600 font-medium">Dars boshqaruvi</span>
                                </div>
                                <div className="flex gap-2">
                                    <Button variant="outline" size="sm" className="text-blue-600 border-blue-200">
                                        <Edit className="w-4 h-4 mr-1"/> Tahrirlash
                                    </Button>
                                    <Button variant="outline" size="sm" className="text-red-600 border-red-200">
                                        <Trash2 className="w-4 h-4 mr-1"/> O'chirish
                                    </Button>
                                </div>
                            </div>
                        )}

                        <div className="p-6 border-b">
                            <div className="flex items-start justify-between mb-4">
                                <div className="flex-1">
                                    <h1 className="text-2xl font-bold" style={{color: '#0F3460'}}>{selectedLesson.title}</h1>
                                    <p className="text-gray-600">{data.courseTitle} • Dars {selectedLesson.id}</p>
                                </div>
                                <Badge className={selectedLesson.completed ? "bg-green-500" : "bg-[#FF6B35]"}>
                                    {selectedLesson.completed ? 'Tugallangan' : 'Jarayonda'}
                                </Badge>
                            </div>

                            <div className="bg-gray-50 rounded-lg p-4">
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-sm font-medium">Kurs progressi</span>
                                    <span className="text-sm font-bold" style={{color: '#0F3460'}}>
                                        {data.completedCount}/{data.totalCount} Dars
                                    </span>
                                </div>
                                <Progress value={data.progressPercent} className="h-2"/>
                            </div>
                        </div>

                        {/* Uy vazifasi faqat student uchun */}
                        {!isAdmin && (
                            <div className="p-6">
                                <div className="border-2 border-dashed border-gray-200 rounded-xl p-6">
                                    <div className="flex items-center gap-3 mb-4">
                                        <div className="w-12 h-12 rounded-full bg-[#FF6B35] flex items-center justify-center">
                                            <FileText className="w-6 h-6 text-white"/>
                                        </div>
                                        <h3 className="text-xl font-bold" style={{color: '#0F3460'}}>Uy vazifasi</h3>
                                    </div>

                                    {!homeworkSubmitted ? (
                                        <div className="space-y-4">
                                            <Textarea
                                                placeholder="Javobingizni shu yerga yozing..."
                                                rows={5}
                                                value={homeworkText}
                                                onChange={(e) => setHomeworkText(e.target.value)}
                                            />
                                            <Button
                                                className="w-full bg-[#FF6B35] hover:bg-[#E55A2B]"
                                                onClick={handleSubmit}
                                                disabled={!homeworkText.trim()}
                                            >
                                                <Upload className="mr-2 h-4 w-4"/> Topshirish
                                            </Button>
                                        </div>
                                    ) : (
                                        <div className="text-center py-4 text-green-600 font-bold">
                                            <CheckCircle2 className="w-12 h-12 mx-auto mb-2"/>
                                            Vazifa muvaffaqiyatli yuborildi!
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Kurs Mundarijasi (Sidebar) */}
                    <div className="lg:col-span-1 bg-white lg:rounded-xl shadow-lg overflow-hidden flex flex-col h-fit">
                        <div className="p-6 text-white" style={{background: 'linear-gradient(135deg, #0F3460 0%, #1A4D7A 100%)'}}>
                            <h2 className="text-xl font-bold">Kurs mundarijasi</h2>
                            <p className="text-sm opacity-80">{data.totalCount} ta dars</p>
                        </div>

                        <div className="overflow-y-auto max-h-[calc(100vh-250px)]">
                            {data.sections.map((section) => (
                                <div key={section.id.toString()} className="border-b">
                                    <button
                                        onClick={() => setExpandedSection(expandedSection === Number(section.id) ? null : Number(section.id))}
                                        className="w-full px-6 py-4 flex items-center justify-between hover:bg-gray-50"
                                    >
                                        <span className="font-bold text-[#0F3460]">{section.title}</span>
                                        {expandedSection === Number(section.id) ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
                                    </button>

                                    {expandedSection === Number(section.id) && (
                                        <div className="bg-gray-50">
                                            {section.lessons.map((lesson) => (
                                                <button
                                                    key={lesson.id.toString()}
                                                    onClick={() => handleLessonClick(lesson)}
                                                    disabled={lesson.locked && !isAdmin}
                                                    className={`w-full px-6 py-4 flex items-center gap-4 text-left border-l-4
                                                        ${selectedLesson?.id === lesson.id ? 'bg-[#FF6B35]/10 border-[#FF6B35]' : 'hover:bg-white border-transparent'}
                                                        ${lesson.locked && !isAdmin ? 'opacity-60 cursor-not-allowed' : 'cursor-pointer'}
                                                    `}
                                                >
                                                    <div className="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
                                                         style={{ backgroundColor: (lesson.locked && !isAdmin) ? '#e5e7eb' : lesson.completed ? '#10b981' : '#FF6B35', color: 'white' }}>
                                                        {(lesson.locked && !isAdmin) ? <Lock className="w-5 h-5" /> : lesson.completed ? <CheckCircle2 className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                                                    </div>
                                                    <div className="flex-1">
                                                        <h4 className="text-sm font-bold truncate" style={{ color: '#0F3460' }}>{lesson.title}</h4>
                                                        <span className="text-xs text-gray-500">{lesson.duration}</span>
                                                    </div>
                                                </button>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}