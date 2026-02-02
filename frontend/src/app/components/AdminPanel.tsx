import { useState } from 'react';
import { PlusCircle, Video, Save, Loader2 } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { addLesson } from '../../services/courseService';

export function AdminPanel() {
    const [courseId, setCourseId] = useState('');
    const [lessonTitle, setLessonTitle] = useState('');
    const [videoUrl, setVideoUrl] = useState('');
    const [loading, setLoading] = useState(false);
    const [courseTitle, setCourseTitle] = useState('');

    // @ts-ignore
    const handleAddLesson = async () => {
        // Maydonlarni tekshirish
        if (!courseId || !lessonTitle || !videoUrl) {
            alert("Barcha maydonlarni to'ldiring!");
            return;
        }

        try {
            setLoading(true);
            await addLesson({
                courseId: Number(courseId),
                title: lessonTitle,
                videoUrl: videoUrl,
                duration: "10:00"
            });

            alert("Dars muvaffaqiyatli qo'shildi!");

            setLessonTitle('');
            setVideoUrl('');
        } catch (error) {
            console.error("Dars qo'shishda xato:", error);
            alert("Dars qo'shishda xatolik yuz berdi!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-8 bg-gray-50 min-h-screen">
            <h1 className="text-3xl font-bold mb-8 text-[#0F3460]">Admin Dashboard</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="bg-white p-6 rounded-xl shadow-md">
                    <div className="flex items-center gap-3 mb-6">
                        <PlusCircle className="text-[#FF6B35]" />
                        <h2 className="text-xl font-bold">Yangi Kurs Yaratish</h2>
                    </div>
                    <div className="space-y-4">
                        <Input
                            placeholder="Kurs nomi"
                            value={courseTitle}
                            onChange={(e) => setCourseTitle(e.target.value)}
                        />
                        <Button className="w-full bg-[#0F3460]">Kursni Saqlash</Button>
                    </div>
                </div>

                <div className="bg-white p-6 rounded-xl shadow-md">
                    <div className="flex items-center gap-3 mb-6">
                        <Video className="text-[#FF6B35]" />
                        <h2 className="text-xl font-bold">Dars Yuklash (MinIO)</h2>
                    </div>
                    <div className="space-y-4">
                        <Input
                            placeholder="Kurs ID (raqam)"
                            type="number"
                            value={courseId}
                            onChange={(e) => setCourseId(e.target.value)}
                        />
                        <Input
                            placeholder="Dars sarlavhasi"
                            value={lessonTitle}
                            onChange={(e) => setLessonTitle(e.target.value)}
                        />
                        <Input
                            placeholder="MinIO Video URL (http://localhost:9000/...)"
                            value={videoUrl}
                            onChange={(e) => setVideoUrl(e.target.value)}
                        />
                        <Button
                            onClick={handleAddLesson}
                            className="w-full bg-[#FF6B35]"
                            disabled={loading}
                        >
                            {loading ? (
                                <Loader2 className="animate-spin mr-2 h-4 w-4" />
                            ) : (
                                <Save className="mr-2 h-4 w-4" />
                            )}
                            Bazaga saqlash
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
}