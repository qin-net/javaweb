import MockAdapter from 'axios-mock-adapter';
import axios from 'axios';
import { mockPapers, mockAuthors, mockReviewers, mockReviews } from './data';

const mock = new MockAdapter(axios, { delayResponse: 200 });

function ok(data: any): [number, Record<string, unknown>] {
  return [200, { code: 200, message: 'success', data }];
}

function notFound(): [number, Record<string, unknown>] {
  return [404, { code: 404, message: 'not found', data: null }];
}

// ── Dashboard ──
mock.onGet('/api/dashboard/stats').reply(() => {
  const byJournal = (['工学版', '理学版', '文科版', '生物医学版'] as const).map((name) => ({
    name,
    count: mockPapers.filter((p) => p.journalName === name).length,
  }));

  const monthlyMap: Record<string, { month: string; submissions: number; accepted: number }> = {};
  mockPapers.forEach((p) => {
    const m = p.submissionDate.substring(0, 7);
    if (!monthlyMap[m]) monthlyMap[m] = { month: m, submissions: 0, accepted: 0 };
    monthlyMap[m].submissions++;
    if (p.status === 'accepted' || p.status === 'published') monthlyMap[m].accepted++;
  });
  const monthlyTrend = Object.values(monthlyMap)
    .sort((a, b) => a.month.localeCompare(b.month))
    .slice(-12);

  const recentPapers = [...mockPapers]
    .sort((a, b) => new Date(b.submissionDate).getTime() - new Date(a.submissionDate).getTime())
    .slice(0, 10);

  return ok({
    totalSubmissions: mockPapers.length,
    underReview: mockPapers.filter((p) => p.status === 'reviewing').length,
    accepted: mockPapers.filter((p) => p.status === 'accepted').length,
    published: mockPapers.filter((p) => p.status === 'published').length,
    byJournal,
    monthlyTrend,
    recentPapers,
  });
});

// ── Papers ──
mock.onGet('/api/papers').reply((config) => {
  const params = config.params || {};
  let result = [...mockPapers];
  if (params.keyword) {
    const kw = params.keyword.toLowerCase();
    result = result.filter(
      (p) =>
        p.title.toLowerCase().includes(kw) ||
        p.authorName.toLowerCase().includes(kw) ||
        p.abstract.toLowerCase().includes(kw)
    );
  }
  if (params.journalName) result = result.filter((p) => p.journalName === params.journalName);
  if (params.status) result = result.filter((p) => p.status === params.status);
  if (params.authorId) result = result.filter((p) => p.authorId === params.authorId);

  result.sort((a, b) => new Date(b.submissionDate).getTime() - new Date(a.submissionDate).getTime());

  const page = parseInt(params.page) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  const total = result.length;
  const start = (page - 1) * pageSize;

  return ok({ items: result.slice(start, start + pageSize), total, page, pageSize });
});

mock.onGet(new RegExp('/api/papers/(\\w+)/reviews$')).reply((config) => {
  const id = config.url!.match(/\/api\/papers\/(\w+)\/reviews$/)![1];
  return ok(mockReviews.filter((r) => r.paperId === id));
});

mock.onGet(new RegExp('/api/papers/(\\w+)$')).reply((config) => {
  const id = config.url!.match(/\/api\/papers\/(\w+)$/)![1];
  const paper = mockPapers.find((p) => p.id === id);
  return paper ? ok(paper) : notFound();
});

mock.onPost('/api/papers').reply((config) => {
  const data = JSON.parse(config.data);
  const newPaper = {
    ...data,
    id: 'p' + (mockPapers.length + 1),
    submissionDate: new Date().toISOString().split('T')[0],
    status: 'submitted' as const,
    keywords: data.keywords || [],
    references: data.references || [],
  };
  mockPapers.push(newPaper);
  return ok(newPaper);
});

mock.onPut(new RegExp('/api/papers/(\\w+)/accept$')).reply((config) => {
  const id = config.url!.match(/\/api\/papers\/(\w+)\/accept$/)![1];
  const paper = mockPapers.find((p) => p.id === id);
  if (!paper) return notFound();
  paper.status = 'accepted';
  paper.acceptanceDate = new Date().toISOString().split('T')[0];
  return ok(paper);
});

mock.onPut(new RegExp('/api/papers/(\\w+)$')).reply((config) => {
  const id = config.url!.match(/\/api\/papers\/(\w+)$/)![1];
  const idx = mockPapers.findIndex((p) => p.id === id);
  if (idx === -1) return notFound();
  const data = JSON.parse(config.data);
  mockPapers[idx] = { ...mockPapers[idx], ...data };
  return ok(mockPapers[idx]);
});

// ── Reviews ──
mock.onPost('/api/reviews').reply((config) => {
  const data = JSON.parse(config.data);
  const newReview = {
    ...data,
    id: 'rev' + (mockReviews.length + 1),
    reviewDate: new Date().toISOString().split('T')[0],
  };
  mockReviews.push(newReview);
  const paper = mockPapers.find((p: any) => p.id === data.paperId);
  if (paper) {
    paper.status = data.decision === 'approved' ? 'accepted' : 'rejected';
    paper.reviewDate = newReview.reviewDate;
    if (data.decision === 'approved') paper.acceptanceDate = newReview.reviewDate;
  }
  return ok(newReview);
});

// ── Authors ──
mock.onGet('/api/authors').reply((config) => {
  const keyword = config.params?.keyword?.toLowerCase();
  const result = keyword
    ? mockAuthors.filter(
        (a) =>
          a.name.toLowerCase().includes(keyword) ||
          a.email.toLowerCase().includes(keyword) ||
          a.institution.toLowerCase().includes(keyword)
      )
    : mockAuthors;
  return ok(result);
});

mock.onGet(new RegExp('/api/authors/(\\w+)/papers$')).reply((config) => {
  const id = config.url!.match(/\/api\/authors\/(\w+)\/papers$/)![1];
  return ok(mockPapers.filter((p) => p.authorId === id));
});

mock.onGet(new RegExp('/api/authors/(\\w+)$')).reply((config) => {
  const id = config.url!.match(/\/api\/authors\/(\w+)$/)![1];
  const author = mockAuthors.find((a) => a.id === id);
  return author ? ok(author) : notFound();
});

// ── Reviewers ──
mock.onGet('/api/reviewers').reply((config) => {
  const keyword = config.params?.keyword?.toLowerCase();
  const result = keyword
    ? mockReviewers.filter(
        (r) =>
          r.name.toLowerCase().includes(keyword) ||
          r.email.toLowerCase().includes(keyword) ||
          r.institution.toLowerCase().includes(keyword) ||
          r.specialty.toLowerCase().includes(keyword)
      )
    : mockReviewers;
  return ok(result);
});

mock.onGet(new RegExp('/api/reviewers/(\\w+)/reviews$')).reply((config) => {
  const id = config.url!.match(/\/api\/reviewers\/(\w+)\/reviews$/)![1];
  return ok(mockReviews.filter((r) => r.reviewerId === id));
});

mock.onGet(new RegExp('/api/reviewers/(\\w+)$')).reply((config) => {
  const id = config.url!.match(/\/api\/reviewers\/(\w+)$/)![1];
  const reviewer = mockReviewers.find((r) => r.id === id);
  return reviewer ? ok(reviewer) : notFound();
});

// ── Assignments ──
mock.onPost('/api/assignments').reply((config) => {
  const { paperId, reviewerId, reviewerName } = JSON.parse(config.data);
  const paper = mockPapers.find((p) => p.id === paperId);
  if (!paper) return notFound();
  paper.assignedReviewerId = reviewerId;
  paper.assignedReviewerName = reviewerName;
  paper.status = 'reviewing';
  return ok({ success: true });
});

mock.onGet('/api/assignments').reply(() => {
  return ok(mockPapers.filter((p) => p.assignedReviewerId));
});

export default mock;
