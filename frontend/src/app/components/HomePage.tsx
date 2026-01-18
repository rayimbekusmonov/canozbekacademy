import { useEffect, useState } from 'react';
import { Search, BookOpen, Clock, Users, Star, TrendingUp, Loader2 } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Badge } from './ui/badge';
// @ts-ignore
import { getCourses, CourseResponse } from '../services/courseService';
// @ts-ignore
import { ImageWithFallback } from './ImageWithFallback';

interface HomePageProps {
    onNavigate: (page: string, courseId?: number) => void;
}

export function HomePage({ onNavigate }: HomePageProps) {
    const [featuredCourses, setFeaturedCourses] = useState<CourseResponse[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // @ts-ignore
        const fetchFeatured = async () => {
            try {
                setLoading(true);
                const data = await getCourses();
                setFeaturedCourses(data.slice(0, 3));
            } catch (error) {
                console.error("Kurslarni yuklashda xatolik:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchFeatured();
    }, []);

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Hero Section */}
            <section className="relative overflow-hidden" style={{ background: 'linear-gradient(135deg, #0F3460 0%, #1A4D7A 100%)' }}>
                <div className="absolute inset-0 opacity-10">
                    <div className="absolute top-20 left-20 w-72 h-72 bg-white rounded-full blur-3xl"></div>
                    <div className="absolute bottom-20 right-20 w-96 h-96 bg-[#FF6B35] rounded-full blur-3xl"></div>
                </div>

                <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 sm:py-24">
                    <div className="text-center">
                        <Badge className="mb-4 bg-[#FF6B35] hover:bg-[#E55A2B] text-white border-0">
                            🎓 #1 Educational Platform in Uzbekistan
                        </Badge>
                        <h1 className="text-4xl sm:text-5xl lg:text-6xl tracking-tight text-white mb-6">
                            Learn Turkish with
                            <span className="block mt-2" style={{ color: '#FF6B35' }}>Expert Instructors</span>
                        </h1>
                        <p className="text-lg sm:text-xl text-gray-200 max-w-2xl mx-auto mb-8">
                            Master Turkish language, prepare for exams, and unlock new opportunities with our comprehensive online courses.
                        </p>

                        {/* Search Bar */}
                        <div className="max-w-3xl mx-auto mb-8">
                            <div className="relative">
                                <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                                <Input
                                    placeholder="Search courses: Turkish, Math, Russian, English..."
                                    className="pl-12 pr-4 py-6 text-lg rounded-xl border-0 shadow-xl"
                                />
                            </div>
                        </div>

                        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                            <Button
                                size="lg"
                                onClick={() => onNavigate('catalog')}
                                className="px-8 py-6 text-lg rounded-xl shadow-lg hover:shadow-xl transition-all"
                                style={{ backgroundColor: '#FF6B35', color: 'white' }}
                            >
                                Start Free Lesson
                            </Button>
                            <Button
                                size="lg"
                                variant="outline"
                                onClick={() => onNavigate('catalog')}
                                className="px-8 py-6 text-lg bg-white/10 backdrop-blur-sm text-white border-white/30 hover:bg-white/20 rounded-xl"
                            >
                                Browse Courses
                            </Button>
                        </div>
                    </div>

                    {/* Stats */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mt-16 max-w-4xl mx-auto">
                        {[
                            { icon: Users, label: 'Active Students', value: '15,000+' },
                            { icon: BookOpen, label: 'Courses', value: '50+' },
                            { icon: Clock, label: 'Hours Content', value: '500+' },
                            { icon: TrendingUp, label: 'Success Rate', value: '95%' }
                        ].map((stat, idx) => (
                            <div key={idx} className="text-center">
                                <stat.icon className="w-8 h-8 mx-auto mb-2" style={{ color: '#FF6B35' }} />
                                <div className="text-2xl sm:text-3xl text-white mb-1">{stat.value}</div>
                                <div className="text-sm text-gray-300">{stat.label}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Featured Courses */}
            <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h2 className="text-3xl sm:text-4xl mb-2" style={{ color: '#0F3460' }}>Featured Turkish Courses</h2>
                        <p className="text-gray-600">Start your journey to fluency today</p>
                    </div>
                    <Button
                        variant="ghost"
                        onClick={() => onNavigate('catalog')}
                        style={{ color: '#FF6B35' }}
                    >
                        View All →
                    </Button>
                </div>

                {loading ? (
                    <div className="flex justify-center py-20">
                        <Loader2 className="w-10 h-10 animate-spin text-[#FF6B35]" />
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {featuredCourses.map((course) => (
                            <div
                                key={course.id}
                                className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all overflow-hidden cursor-pointer group"
                                onClick={() => onNavigate('dashboard', course.id)}
                            >
                                <div className="relative h-48 overflow-hidden">
                                    <ImageWithFallback
                                        src={course.thumbnailUrl}
                                        alt={course.title}
                                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                    />
                                    <div className="absolute top-4 right-4">
                                        <Badge className="bg-white/90 text-gray-800 border-0">
                                            {course.level}
                                        </Badge>
                                    </div>
                                </div>

                                <div className="p-6">
                                    <div className="flex items-center gap-2 mb-3">
                                        <Badge variant="outline" style={{ borderColor: '#0F3460', color: '#0F3460' }}>
                                            {course.category}
                                        </Badge>
                                    </div>

                                    <h3 className="text-xl mb-2 font-bold line-clamp-1">{course.title}</h3>
                                    <p className="text-gray-600 text-sm mb-4">by {course.teacherName}</p>

                                    <div className="flex items-center justify-between mb-4">
                                        <div className="flex items-center gap-1">
                                            <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                            <span>{Number(course.rating).toFixed(1)}</span>
                                        </div>
                                        <div className="flex items-center gap-1 text-gray-600 text-sm">
                                            <Users className="w-4 h-4" />
                                            <span>{course.totalStudents?.toLocaleString() || 0}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between pt-4 border-t">
                                        <div>
                      <span className="text-2xl font-bold" style={{ color: '#0F3460' }}>
                        {course.price?.toLocaleString()} UZS
                      </span>
                                        </div>
                                        <Button
                                            size="sm"
                                            style={{ backgroundColor: '#FF6B35', color: 'white' }}
                                        >
                                            Enroll Now
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            {/* Nima uchun biz? (Statik qism) */}
            <section className="bg-white py-16">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-3xl sm:text-4xl mb-4" style={{ color: '#0F3460' }}>Why Choose canozbek.uz?</h2>
                        <p className="text-gray-600 max-w-2xl mx-auto">
                            We provide the best learning experience with expert instructors and cutting-edge technology
                        </p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {[
                            {
                                title: 'Expert Instructors',
                                description: 'Learn from certified professionals with years of teaching experience',
                                icon: '👨‍🏫'
                            },
                            {
                                title: 'Interactive Learning',
                                description: 'Engage with practical exercises, quizzes, and real-world applications',
                                icon: '🎯'
                            },
                            {
                                title: 'Flexible Schedule',
                                description: 'Study at your own pace with 24/7 access to all course materials',
                                icon: '⏰'
                            }
                        ].map((feature, idx) => (
                            <div
                                key={idx}
                                className="text-center p-8 rounded-xl border-2 border-gray-100 hover:border-[#FF6B35] transition-all"
                            >
                                <div className="text-5xl mb-4">{feature.icon}</div>
                                <h3 className="text-xl mb-3 font-bold" style={{ color: '#0F3460' }}>{feature.title}</h3>
                                <p className="text-gray-600">{feature.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>
        </div>
    );
}