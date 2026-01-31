// src/components/CourseCatalog.tsx
import { useEffect, useState } from 'react';
import { Star, Users, Clock, Filter, Loader2 } from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { getCourses, CourseResponse } from '../../services/courseService';
// @ts-ignore
// import { ImageWithFallback } from 'figma/ImageWithFallback';
import { ImageWithFallback } from './ImageWithFallback';

interface CourseCatalogProps {
    onNavigate: (page: string, courseId?: number) => void;
}

export function CourseCatalog({ onNavigate }: CourseCatalogProps) {
    const [courses, setCourses] = useState<CourseResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // @ts-ignore
        const fetchCourses = async () => {
            try {
                setLoading(true);
                const data = await getCourses();
                setCourses(data);
                setError(null);
            } catch (err) {
                setError("Kurslarni yuklab bo'lmadi. Server yoqilganini tekshiring.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchCourses();
    }, []);

    if (loading) {
        return (
            <div className="flex h-[60vh] items-center justify-center">
                <Loader2 className="w-10 h-10 animate-spin text-[#FF6B35]" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center p-20 text-red-500">
                <p className="text-xl font-bold">{error}</p>
                <Button onClick={() => window.location.reload()} className="mt-4">Qayta urinish</Button>
            </div>
        );
    }

    return (
        <div className="bg-gray-50 min-h-screen py-12">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h1 className="text-3xl font-bold mb-2" style={{ color: '#0F3460' }}>Barcha kurslar</h1>
                        <p className="text-gray-600">O'zingizga mos keladigan kursni tanlang</p>
                    </div>
                    <Button variant="outline" className="flex items-center gap-2">
                        <Filter className="w-4 h-4" /> Filter
                    </Button>
                </div>

                {courses.length === 0 ? (
                    <div className="text-center py-20 text-gray-500 text-lg italic">
                        Hozircha kurslar mavjud emas.
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {courses.map((course) => (
                            <div
                                key={course.id}
                                className="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow border border-gray-100"
                            >
                                <div className="relative aspect-video">
                                    <ImageWithFallback
                                        src={course.thumbnailUrl}
                                        alt={course.title}
                                        className="w-full h-full object-cover"
                                    />
                                    <Badge className="absolute top-4 left-4 bg-white/90 text-[#0F3460] hover:bg-white border-none shadow-sm">
                                        {course.category}
                                    </Badge>
                                </div>

                                <div className="p-6">
                                    <h3 className="text-xl font-bold mb-2 line-clamp-1" style={{ color: '#0F3460' }}>
                                        {course.title}
                                    </h3>

                                    <div className="flex items-center gap-4 text-sm text-gray-500 mb-4">
                                        <div className="flex items-center gap-1">
                                            <Users className="w-4 h-4" />
                                            <span>{course.totalStudents || 0}</span>
                                        </div>
                                        <div className="flex items-center gap-1">
                                            <Clock className="w-4 h-4" />
                                            <span>{course.durationWeeks || 0} hafta</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center gap-2 mb-4">
                                        <div className="flex items-center gap-1">
                                            <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                            <span>{course.rating ? Number(course.rating).toFixed(1) : "0.0"}</span>
                                        </div>
                                        <span className="text-gray-400 text-sm">
                      ({course.totalReviews || 0} sharhlar)
                    </span>
                                    </div>

                                    <div className="flex items-center justify-between pt-4 border-t">
                                        <div>
                      <span className="text-2xl font-bold" style={{ color: '#0F3460' }}>
                        {course.price?.toLocaleString()}
                      </span>
                                            <span className="text-gray-600 text-sm ml-1">UZS</span>
                                        </div>
                                        <Button
                                            size="sm"
                                            className="bg-[#FF6B35] hover:bg-[#E55A2B] text-white font-medium"
                                            onClick={() => onNavigate('dashboard', course.id)}
                                        >
                                            Kursni ko'rish
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}