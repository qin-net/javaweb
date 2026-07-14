// Journal type
export type JournalName = '工学版' | '理学版' | '文科版' | '生物医学版';

// Paper status
export type PaperStatus = 'submitted' | 'reviewing' | 'accepted' | 'rejected' | 'published';

// Review decision
export type ReviewDecision = 'approved' | 'rejected';

// Reference
export interface Reference {
  id: string;
  title: string;
  authors: string;
  journal: string;
  year: number;
  volume?: string;
  issue?: string;
  pages?: string;
  doi?: string;
}

// Paper
export interface Paper {
  id: string;
  title: string;
  abstract: string;
  keywords: string[];
  content: string;
  references: Reference[];
  journalName: JournalName;
  authorId: string;
  authorName: string;
  status: PaperStatus;
  submissionDate: string;
  reviewDate?: string;
  acceptanceDate?: string;
  publicationDate?: string;
  assignedReviewerId?: string;
  assignedReviewerName?: string;
}

// Review
export interface Review {
  id: string;
  paperId: string;
  paperTitle: string;
  reviewerId: string;
  reviewerName: string;
  decision: ReviewDecision;
  comments: string;
  reviewDate: string;
}

// Author
export interface Author {
  id: string;
  name: string;
  email: string;
  institution: string;
  department: string;
  phone: string;
  paperIds: string[];
}

// Reviewer
export interface Reviewer {
  id: string;
  name: string;
  email: string;
  institution: string;
  department: string;
  specialty: string;
  phone: string;
  reviewIds: string[];
}

// Assignment
export interface Assignment {
  id: string;
  paperId: string;
  paperTitle: string;
  reviewerId: string;
  reviewerName: string;
  assignedDate: string;
}

// Dashboard stats
export interface DashboardStats {
  totalSubmissions: number;
  underReview: number;
  accepted: number;
  published: number;
  byJournal: { name: JournalName; count: number }[];
  monthlyTrend: { month: string; submissions: number; accepted: number }[];
  recentPapers: Paper[];
}

// API response wrapper
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// Pagination
export interface PaginatedData<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}

// Paper filter params
export interface PaperFilter {
  keyword?: string;
  journalName?: JournalName;
  status?: PaperStatus;
  authorId?: string;
  page?: number;
  pageSize?: number;
}
