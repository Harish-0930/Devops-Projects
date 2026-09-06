import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { adminLogin } from '../../services/authService';

const AdminLogin = () => {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await adminLogin(form);
      login({ ...res.data, role: 'ADMIN' });
      navigate('/admin/dashboard');
    } catch (err) { setError('Invalid admin credentials'); }
  };

  return (
    <div className="container login-container">
      <div className="card p-4">
        <h3 className="text-center mb-4">Admin Login</h3>
        {error && <div className="alert alert-danger">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="mb-3"><label>Username</label><input className="form-control" value={form.username} onChange={e => setForm({...form, username: e.target.value})} required /></div>
          <div className="mb-3"><label>Password</label><input type="password" className="form-control" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required /></div>
          <button type="submit" className="btn btn-dark w-100">Login as Admin</button>
        </form>
      </div>
    </div>
  );
};

export default AdminLogin;