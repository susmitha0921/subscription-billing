import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import CustomerDashboard from './pages/CustomerDashboard';

function App() {
  const { user, loading, logout } = useAuth();

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      {user && (
        <div className="nav">
          <h2>Billing System</h2>
          <button onClick={logout}>Logout</button>
        </div>
      )}
      <div className="container">
        <Routes>
          <Route path="/login" element={!user ? <Login /> : <Navigate to="/" />} />
          <Route path="/register" element={!user ? <Register /> : <Navigate to="/" />} />
          <Route path="/admin" element={user?.role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/login" />} />
          <Route path="/customer" element={user?.role === 'CUSTOMER' ? <CustomerDashboard /> : <Navigate to="/login" />} />
          <Route path="/" element={
            user ? (
              user.role === 'ADMIN' ? <Navigate to="/admin" /> : <Navigate to="/customer" />
            ) : <Navigate to="/login" />
          } />
        </Routes>
      </div>
    </div>
  );
}

export default App;
