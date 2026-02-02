// src/services/courseService.ts
import api from './api';

export interface CourseResponse {
    id: number;
    title: string;
    slug: string;
    description: string;
    thumbnailUrl: string;
    category: string;
    level: string;
    language: string;
    price: number;
    discountPrice: number;
    durationWeeks: number;
    totalLessons: number;
    teacherName: string;
    teacherAvatar: string;
    teacherSpecialization: string;
    rating: number;
    totalStudents: number;
    totalReviews: number;
    createdAt: string;
}

export const getCourses = async () => {
    const response = await api.get<CourseResponse[]>('/courses');
    return response.data;
};
// src/services/courseService.ts

export interface LessonRequest {
    courseId: number;
    title: string;
    videoUrl: string; // MinIO'dagi havola: http://localhost:9000/canozbek-files/video.mp4
    duration: string;
}

export const addLesson = async (lessonData: LessonRequest) => {
    // api.ts da yaratilgan axios instance orqali backendga POST so'rov yuboramiz
    const response = await api.post('/lessons/add', lessonData);
    return response.data;
};