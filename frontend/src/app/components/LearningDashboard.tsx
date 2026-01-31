// src/components/LearningDashboard.tsx
import { Key, useEffect, useState} from 'react';
import {Play, Lock, CheckCircle2, ChevronDown, ChevronUp, Upload, FileText, Loader2} from 'lucide-react';
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
    const [expandedSection, setExpandedSection] = useState<number | null>(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // @ts-ignore
        const fetchDashboard = async () => {
            try {
                setLoading(true);
                const result = await getDashboardData(courseId);
                setData(result);

                // Birinchi bo'limning birinchi darsini avtomatik tanlash
                if (result.sections.length > 0 && result.sections[0].lessons.length > 0) {
                    setSelectedLesson(result.sections[0].lessons[0]);
                    setExpandedSection(result.sections[0].id);
                }
            } catch (error) {
                console.error("Dashboard ma'lumotlarini yuklashda xato:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchDashboard();
    }, [courseId]);

    const handleLessonClick = (lesson: {
        id: { toString: () => React.Key };
        locked: boolean;
        completed: any;
        title: unknown;
        duration: unknown
    }) => {
        if (!lesson.locked) {
            // @ts-ignore
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
                // Progressni yangilash uchun ma'lumotni qayta fetch qilish mumkin
            } catch (error) {
                alert("Xatolik yuz berdi. Qaytadan urining.");
            }
        }
    };

    if (loading) {
        return (
            <div className="flex h-screen items-center justify-center">
                <Loader2 className="w-12 h-12 animate-spin text-[#FF6B35]"/>
            </div>
        );
    }

    if (!data || !selectedLesson) return <div className="p-20 text-center">Ma'lumot topilmadi.</div>;

    // @ts-ignore
    return (
        <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
            <div className="max-w-[1920px] mx-auto">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-0 lg:gap-6 lg:p-6">

                    <div className="lg:col-span-2 bg-white lg:rounded-xl overflow-hidden shadow-lg">
                        <div className="relative bg-black aspect-video">
                            <iframe
                                className="w-full h-full"
                                src={`https://www.youtube.com/embed/${selectedLesson.videoUrl}`}
                                title={selectedLesson.title}
                                allowFullScreen
                            ></iframe>
                        </div>

                        <div className="p-6 border-b">
                            <div className="flex items-start justify-between mb-4">
                                <div className="flex-1">
                                    <h1 className="text-2xl font-bold"
                                        style={{color: '#0F3460'}}>{selectedLesson.title}</h1>
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

                        {/* Uy vazifasi */}
                        <div className="p-6">
                            <div className="border-2 border-dashed border-gray-200 rounded-xl p-6">
                                <div className="flex items-center gap-3 mb-4">
                                    <div
                                        className="w-12 h-12 rounded-full bg-[#FF6B35] flex items-center justify-center">
                                        <FileText className="w-6 h-6 text-white"/>
                                    </div>
                                    <h3 className="text-xl font-bold" style={{color: '#0F3460'}}>Uy vazifasini
                                        topshirish</h3>
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
                    </div>

                    <div className="lg:col-span-1 bg-white lg:rounded-xl shadow-lg overflow-hidden flex flex-col h-fit">
                        <div className="p-6 text-white"
                             style={{background: 'linear-gradient(135deg, #0F3460 0%, #1A4D7A 100%)'}}>
                            <h2 className="text-xl font-bold">Kurs mundarijasi</h2>
                            <p className="text-sm opacity-80">{data.totalCount} ta dars</p>
                        </div>

                        {/* Sidebar - Curriculum qismi */}
                        <div className="overflow-y-auto max-h-[calc(100vh-200px)]">
                            {data.sections.map((section: { id: { toString: () => Key; }; title: unknown; lessons: { id: { toString: () => Key; }; locked: boolean; completed: any; title: unknown; duration: unknown; }[]; }) => (
                                /* Key xatosini yo'qotish uchun .toString() ishlatamiz */
                                <div key={section.id.toString()} className="border-b">
                                    <button
                                        onClick={() => {
                                            // Bu yerda ham id ni aniq number sifatida ko'rsatamiz
                                            const sectionId = Number(section.id);
                                            setExpandedSection(expandedSection === sectionId ? null : sectionId);
                                        }}
                                        className="w-full px-6 py-4 flex items-center justify-between hover:bg-gray-50 transition-colors"
                                    >
                                        <span className="font-bold text-[#0F3460]">{section.title}</span>
                                        {/* expandedSection tekshiruvi */}
                                        {expandedSection === Number(section.id) ? (
                                            <ChevronUp className="w-5 h-5 text-gray-400"/>
                                        ) : (
                                            <ChevronDown className="w-5 h-5 text-gray-400"/>
                                        )}
                                    </button>

                                    {/* Kontent qismi */}
                                    {expandedSection === Number(section.id) && (
                                        <div className="bg-gray-50">
                                            {section.lessons.map((lesson: { id: { toString: () => Key; }; locked: boolean; completed: any; title: unknown; duration: unknown; }) => (
                                                <button
                                                    key={lesson.id.toString()}
                                                    onClick={() => handleLessonClick(lesson)}
                                                    disabled={lesson.locked}
                                                    className={`
                w-full px-6 py-4 flex items-center gap-4 text-left transition-all
                ${selectedLesson?.id === lesson.id ? 'bg-[#FF6B35]/10 border-l-4' : 'hover:bg-white border-l-4 border-transparent'}
                ${lesson.locked ? 'opacity-60 cursor-not-allowed' : 'cursor-pointer'}
              `}
                                                    style={selectedLesson?.id === lesson.id ? { borderLeftColor: '#FF6B35' } : {}}
                                                >
                                                    {/* Dars iconkasi va boshqa qismlar... */}
                                                    <div
                                                        className="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
                                                        style={{
                                                            backgroundColor: lesson.completed ? '#10b981' : lesson.locked ? '#e5e7eb' : '#FF6B35',
                                                            color: 'white'
                                                        }}
                                                    >
                                                        {lesson.locked ? <Lock className="w-5 h-5" /> : lesson.completed ? <CheckCircle2 className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                                                    </div>
                                                    <div className="flex-1 min-w-0">
                                                        <h4 className="text-sm font-bold mb-1 line-clamp-2" style={{ color: '#0F3460' }}>{lesson.title}</h4>
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