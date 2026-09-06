import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { customerRegister } from '../services/authService';

const Register = () => {
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', phoneNumber: '', username: '', password: '', address: { street: '', city: '', state: '', country: '', pincode: '' } });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await customerRegister(form);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  const updateAddress = (field, value) => {
    setForm({...form, address: {...form.address, [field]: value}});
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card p-4">
            <h3 className="text-center mb-4">Create Account</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6 mb-3"><label className="form-label">First Name</label><input className="form-control" value={form.firstName} onChange={e => setForm({...form, firstName: e.target.value})} required /></div>
                <div className="col-md-6 mb-3"><label className="form-label">Last Name</label><input className="form-control" value={form.lastName} onChange={e => setForm({...form, lastName: e.target.value})} required /></div>
              </div>
              <div className="row">
                <div className="col-md-6 mb-3"><label className="form-label">Email</label><input type="email" className="form-control" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required /></div>
                <div className="col-md-6 mb-3"><label className="form-label">Phone</label><input className="form-control" value={form.phoneNumber} onChange={e => setForm({...form, phoneNumber: e.target.value})} required /></div>
              </div>
              <div className="row">
                <div className="col-md-6 mb-3"><label className="form-label">Username</label><input className="form-control" value={form.username} onChange={e => setForm({...form, username: e.target.value})} required /></div>
                <div className="col-md-6 mb-3"><label className="form-label">Password</label><input type="password" className="form-control" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required /></div>
              </div>
              <h5 className="mt-3">Address</h5>
              <div className="row">
                <div className="col-12 mb-3"><label className="form-label">Street</label><input className="form-control" value={form.address.street} onChange={e => updateAddress('street', e.target.value)} required /></div>
                <div className="col-md-4 mb-3"><label className="form-label">City</label><input className="form-control" value={form.address.city} onChange={e => updateAddress('city', e.target.value)} required /></div>
                <div className="col-md-4 mb-3"><label className="form-label">State</label><input className="form-control" value={form.address.state} onChange={e => updateAddress('state', e.target.value)} required /></div>
                <div className="col-md-4 mb-3"><label className="form-label">Pincode</label><input className="form-control" value={form.address.pincode} onChange={e => updateAddress('pincode', e.target.value)} required /></div>
              </div>
              <button type="submit" className="btn btn-primary w-100">Register</button>
            </form>
            <p className="text-center mt-3">Already have an account? <Link to="/login">Login here</Link></p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;