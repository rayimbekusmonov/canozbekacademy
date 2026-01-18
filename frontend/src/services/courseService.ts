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