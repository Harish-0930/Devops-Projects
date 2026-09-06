import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" to="/">NexusBank</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            {!user ? (
              <>
                <li className="nav-item"><Link className="nav-link" to="/login">Login</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/register">Register</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/admin/login">Admin</Link></li>
              </>
            ) : user.role === 'CUSTOMER' ? (
              <>
                <li className="nav-item"><Link className="nav-link" to="/dashboard">Dashboard</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/accounts">Accounts</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/transactions">Transactions</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/transfer">Transfer</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/loans">Loans</Link></li>
                <li className="nav-item"><button className="btn btn-outline-light btn-sm" onClick={handleLogout}>Logout</button></li>
              </>
            ) : (
              <>
                <li className="nav-item"><Link className="nav-link" to="/admin/dashboard">Dashboard</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/admin/search">Search</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/admin/customers">Customers</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/admin/accounts">Accounts</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/admin/loans">Loans</Link></li>
                <li className="nav-item"><button className="btn btn-outline-light btn-sm" onClick={handleLogout}>Logout</button></li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
