// src/services/dashboardService.ts
import api from './api';

export interface Lesson {
    id: number;
    title: string;
    duration: string;
    completed: boolean;
    locked: boolean;
    videoUrl: string;
    description: string;
}

export interface Section {
    id: number;
    title: string;
    lessons: Lesson[];
}

export interface DashboardData {
    courseTitle: string;
    progressPercent: number;
    completedCount: number;
    totalCount: number;
    sections: Section[];
}

// Kurs mundarijasini va progressni olish
export const getDashboardData = async (courseId: number) => {
    const response = await api.get<DashboardData>(`/courses/${courseId}/dashboard`);
    return response.data;
};

// Uy vazifasini topshirish
export const submitHomework = async (lessonId: number, content: string) => {
    const response = await api.post(`/lessons/${lessonId}/homework`, { content });
    return response.data;
};