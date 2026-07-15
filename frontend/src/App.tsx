import { Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import Layout from '@/components/layout/Layout'
import LoadingSkeleton from '@/components/shared/LoadingSkeleton'
import { useAuth } from '@/contexts/AuthContext'
import Login from '@/pages/Login'

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

/** Protected layout: shows spinner during auth check, redirects to /login if not authenticated */
function ProtectedLayout() {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <LoadingSkeleton fullScreen />
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return <Layout />
}

/** Login page: if already logged in, redirect to home */
function LoginRoute() {
  const { user, loading } = useAuth()

  if (loading) {
    return <LoadingSkeleton fullScreen />
  }

  if (user) {
    return <Navigate to="/" replace />
  }

  return <Login />
}

function App() {
  return (
    <Suspense fallback={<LoadingSkeleton fullScreen />}>
      <Routes>
        {/* Public route - not blocked by auth loading */}
        <Route path="/login" element={<LoginRoute />} />

        {/* Protected routes */}
        <Route element={<ProtectedLayout />}>
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

        {/* Catch-all redirect */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  )
}

export default App
