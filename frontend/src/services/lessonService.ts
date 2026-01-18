import api from './api';

export interface LessonResponse {
    id: number;
    title: string;
    description: string;
    videoUrl: string;
    videoDuration: number;
    formattedDuration: string;
    orderIndex: number;
    isPreview: boolean;
    isLocked: boolean;
    isCompleted: boolean;
    videoProgress: number;
    resources: string;
}

export interface SectionResponse {
    id: number;
    title: string;
    description: string;
    orderIndex: number;
    lessons: LessonResponse[];
}

export interface CourseDetailResponse {
    id: number;
    title: string;
    description: string;
    sections: SectionResponse[];
}

export const getCourseDetails = async (courseId: number) => {
    const response = await api.get<CourseDetailResponse>(`/courses/${courseId}`);
    return response.data;
};

export const completeLesson = async (lessonId: number) => {
    const response = await api.post(`/lessons/${lessonId}/complete`);
    return response.data;
};