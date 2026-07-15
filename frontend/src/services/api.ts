import axios, { type AxiosRequestConfig } from 'axios';
import type { Paper, Review, Author, Reviewer, DashboardStats, PaginatedData, PaperFilter, ApiResponse, AuthUser, MenuItem } from '@/types';

// Create axios instance with credentials support
const apiClient = axios.create({
  baseURL: '',
  withCredentials: true,
});

// Response interceptor: auto-redirect to /login on 401
// (skip for /api/auth/me which is used to check session status on page load)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const requestUrl = error.config?.url || '';
    const httpStatus = error.response?.status;
    const responseData = error.response?.data;
    const isAuthCheck = requestUrl.includes('/api/auth/me');

    console.warn(`[API拦截器] 请求失败: ${error.config?.method?.toUpperCase()} ${requestUrl}`, {
      httpStatus,
      responseCode: responseData?.code,
      message: responseData?.message || error.message,
    });

    if (!isAuthCheck && responseData && responseData.code === 401) {
      console.warn('[API拦截器] 收到401，当前页面:', window.location.pathname);
      if (!window.location.pathname.startsWith('/login')) {
        console.warn('[API拦截器] 非登录页，执行硬跳转到 /login');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);

// Helper to extract data from response (handles {code, message, data} wrapper)
async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await apiClient(config);
  const apiResponse = response.data as ApiResponse<T>;
  if (apiResponse.code !== 200) {
    throw new Error(apiResponse.message || '请求失败');
  }
  return apiResponse.data;
}

// ===== Auth APIs =====
export async function login(username: string, password: string): Promise<AuthUser> {
  const response = await apiClient.post('/api/auth/login', { username, password });
  const apiResponse = response.data as ApiResponse<AuthUser>;
  if (apiResponse.code !== 200) {
    throw new Error(apiResponse.message || '登录失败');
  }
  return apiResponse.data;
}

export async function logout(): Promise<void> {
  await apiClient.post('/api/auth/logout');
}

export async function getCurrentUser(): Promise<AuthUser | null> {
  try {
    const response = await apiClient.get('/api/auth/me');
    const apiResponse = response.data as ApiResponse<AuthUser>;
    if (apiResponse.code === 200) {
      return apiResponse.data;
    }
    return null;
  } catch {
    return null;
  }
}

export async function getUserMenus(): Promise<MenuItem[]> {
  const response = await apiClient.get('/api/auth/menus');
  const apiResponse = response.data as ApiResponse<MenuItem[]>;
  if (apiResponse.code !== 200) {
    return [];
  }
  return apiResponse.data;
}

// ===== Dashboard =====
export async function getDashboardStats(): Promise<DashboardStats> {
  return request<DashboardStats>({ method: 'get', url: '/api/dashboard/stats' });
}

// ===== Papers =====
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

// ===== Reviews =====
export async function getPaperReviews(paperId: string): Promise<Review[]> {
  return request<Review[]>({ method: 'get', url: `/api/papers/${paperId}/reviews` });
}

export async function createReview(data: { paperId: string; reviewerId: string; reviewerName: string; decision: 'approved' | 'rejected'; comments: string }): Promise<Review> {
  return request<Review>({ method: 'post', url: '/api/reviews', data });
}

// ===== Authors =====
export async function getAuthors(keyword?: string): Promise<Author[]> {
  return request<Author[]>({ method: 'get', url: '/api/authors', params: keyword ? { keyword } : undefined });
}

export async function getAuthor(id: string): Promise<Author> {
  return request<Author>({ method: 'get', url: `/api/authors/${id}` });
}

export async function getAuthorPapers(authorId: string): Promise<Paper[]> {
  return request<Paper[]>({ method: 'get', url: `/api/authors/${authorId}/papers` });
}

// ===== Reviewers =====
export async function getReviewers(keyword?: string): Promise<Reviewer[]> {
  return request<Reviewer[]>({ method: 'get', url: '/api/reviewers', params: keyword ? { keyword } : undefined });
}

export async function getReviewer(id: string): Promise<Reviewer> {
  return request<Reviewer>({ method: 'get', url: `/api/reviewers/${id}` });
}

export async function getReviewerReviews(reviewerId: string): Promise<Review[]> {
  return request<Review[]>({ method: 'get', url: `/api/reviewers/${reviewerId}/reviews` });
}

// ===== Assignments =====
export async function assignReviewer(paperId: string, reviewerId: string, reviewerName: string): Promise<void> {
  return request<void>({ method: 'post', url: '/api/assignments', data: { paperId, reviewerId, reviewerName } });
}

export async function getAssignments(): Promise<Paper[]> {
  return request<Paper[]>({ method: 'get', url: '/api/assignments' });
}
