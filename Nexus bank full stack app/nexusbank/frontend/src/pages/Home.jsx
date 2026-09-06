import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => (
  <div>
    <div className="hero-section">
      <div className="container">
        <h1 className="display-4 fw-bold mb-4">Welcome to NexusBank</h1>
        <p className="lead mb-4">Your trusted partner for secure online banking</p>
        <div className="d-flex justify-content-center gap-3">
          <Link to="/register" className="btn btn-light btn-lg">Get Started</Link>
          <Link to="/login" className="btn btn-outline-light btn-lg">Sign In</Link>
        </div>
      </div>
    </div>
    <div className="container py-5">
      <div className="row g-4">
        <div className="col-md-4"><div className="card p-4 text-center"><h4>Secure Banking</h4><p>Enterprise-grade security with JWT authentication</p></div></div>
        <div className="col-md-4"><div className="card p-4 text-center"><h4>Instant Transfers</h4><p>Real-time fund transfers between accounts</p></div></div>
        <div className="col-md-4"><div className="card p-4 text-center"><h4>Loan Services</h4><p>Apply for personal, home, and business loans</p></div></div>
      </div>
    </div>
  </div>
);

export default Home;