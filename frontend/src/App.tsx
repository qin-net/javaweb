import { Routes, Route } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import Layout from '@/components/layout/Layout'
import LoadingSkeleton from '@/components/shared/LoadingSkeleton'

const Dashboard = lazy(() => import('@/pages/Dashboard'))
const PaperList = lazy(() => import('@/pages/papers/PaperList'))
const PaperDetail = lazy(() => import('@/pages/papers/PaperDetail'))
const PaperSubmit = lazy(() => import('@/pages/papers/PaperSubmit'))
const PaperReview = lazy(() => import('@/pages/papers/PaperReview'))
const AuthorList = lazy(() => import('@/pages/authors/AuthorList'))
const AuthorDetail = lazy(() => import('@/pages/authors/AuthorDetail'))
const ReviewerList = lazy(() => import('@/pages/reviewers/ReviewerList'))
const ReviewerDetail = lazy(() => import('@/pages/reviewers/ReviewerDetail'))
const AssignmentPage = lazy(() => import('@/pages/AssignmentPage'))
const ReviewManagement = lazy(() => import('@/pages/ReviewManagement'))

function App() {
  return (
    <Suspense fallback={<LoadingSkeleton fullScreen />}>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/papers" element={<PaperList />} />
          <Route path="/papers/submit" element={<PaperSubmit />} />
          <Route path="/papers/:id" element={<PaperDetail />} />
          <Route path="/papers/:id/review" element={<PaperReview />} />
          <Route path="/authors" element={<AuthorList />} />
          <Route path="/authors/:id" element={<AuthorDetail />} />
          <Route path="/reviewers" element={<ReviewerList />} />
          <Route path="/reviewers/:id" element={<ReviewerDetail />} />
          <Route path="/review-management" element={<ReviewManagement />} />
          <Route path="/assignments" element={<AssignmentPage />} />
        </Route>
      </Routes>
    </Suspense>
  )
}

export default App
