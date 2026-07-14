import axios, { type AxiosRequestConfig } from 'axios';
import type { Paper, Review, Author, Reviewer, DashboardStats, PaginatedData, PaperFilter, ApiResponse } from '@/types';

// Make sure mock server is initialized
import '@/mock/server';

// Helper to extract data from response
async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await axios(config);
  return (response.data as ApiResponse<T>).data;
}

// Dashboard
export async function getDashboardStats(): Promise<DashboardStats> {
  return request<DashboardStats>({ method: 'get', url: '/api/dashboard/stats' });
}

// Papers
export async function getPapers(filter?: PaperFilter): Promise<PaginatedData<Paper>> {
  return request<PaginatedData<Paper>>({ method: 'get', url: '/api/papers', params: filter });
}

export async function getPaper(id: string): Promise<Paper> {
  return request<Paper>({ method: 'get', url: `/api/papers/${id}` });
}

export async function createPaper(data: Partial<Paper>): Promise<Paper> {
  return request<Paper>({ method: 'post', url: '/api/papers', data });
}

export async function updatePaper(id: string, data: Partial<Paper>): Promise<Paper> {
  return request<Paper>({ method: 'put', url: `/api/papers/${id}`, data });
}

export async function acceptPaper(id: string): Promise<Paper> {
  return request<Paper>({ method: 'put', url: `/api/papers/${id}/accept` });
}

// Reviews
export async function getPaperReviews(paperId: string): Promise<Review[]> {
  return request<Review[]>({ method: 'get', url: `/api/papers/${paperId}/reviews` });
}

export async function createReview(data: { paperId: string; reviewerId: string; reviewerName: string; decision: 'approved' | 'rejected'; comments: string }): Promise<Review> {
  return request<Review>({ method: 'post', url: '/api/reviews', data });
}

// Authors
export async function getAuthors(keyword?: string): Promise<Author[]> {
  return request<Author[]>({ method: 'get', url: '/api/authors', params: keyword ? { keyword } : undefined });
}

export async function getAuthor(id: string): Promise<Author> {
  return request<Author>({ method: 'get', url: `/api/authors/${id}` });
}

export async function getAuthorPapers(authorId: string): Promise<Paper[]> {
  return request<Paper[]>({ method: 'get', url: `/api/authors/${authorId}/papers` });
}

// Reviewers
export async function getReviewers(keyword?: string): Promise<Reviewer[]> {
  return request<Reviewer[]>({ method: 'get', url: '/api/reviewers', params: keyword ? { keyword } : undefined });
}

export async function getReviewer(id: string): Promise<Reviewer> {
  return request<Reviewer>({ method: 'get', url: `/api/reviewers/${id}` });
}

export async function getReviewerReviews(reviewerId: string): Promise<Review[]> {
  return request<Review[]>({ method: 'get', url: `/api/reviewers/${reviewerId}/reviews` });
}

// Assignments
export async function assignReviewer(paperId: string, reviewerId: string, reviewerName: string): Promise<void> {
  return request<void>({ method: 'post', url: '/api/assignments', data: { paperId, reviewerId, reviewerName } });
}

export async function getAssignments(): Promise<Paper[]> {
  return request<Paper[]>({ method: 'get', url: '/api/assignments' });
}
