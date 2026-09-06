import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import CustomerDashboard from './pages/Customer/Dashboard';
import CustomerProfile from './pages/Customer/Profile';
import CustomerAccounts from './pages/Customer/Accounts';
import CustomerTransactions from './pages/Customer/Transactions';
import Transfer from './pages/Customer/Transfer';
import CustomerLoans from './pages/Customer/Loans';
import AdminLogin from './pages/Admin/AdminLogin';
import AdminDashboard from './pages/Admin/AdminDashboard';
import AdminCustomers from './pages/Admin/Customers';
import AdminAccounts from './pages/Admin/Accounts';
import AdminLoans from './pages/Admin/Loans';
import AdminTransactions from './pages/Admin/Transactions';
import AdminSearch from './pages/Admin/Search';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/dashboard" element={<PrivateRoute role="CUSTOMER"><CustomerDashboard /></PrivateRoute>} />
            <Route path="/profile" element={<PrivateRoute role="CUSTOMER"><CustomerProfile /></PrivateRoute>} />
            <Route path="/accounts" element={<PrivateRoute role="CUSTOMER"><CustomerAccounts /></PrivateRoute>} />
            <Route path="/transactions" element={<PrivateRoute role="CUSTOMER"><CustomerTransactions /></PrivateRoute>} />
            <Route path="/transfer" element={<PrivateRoute role="CUSTOMER"><Transfer /></PrivateRoute>} />
            <Route path="/loans" element={<PrivateRoute role="CUSTOMER"><CustomerLoans /></PrivateRoute>} />
            <Route path="/admin/login" element={<AdminLogin />} />
            <Route path="/admin/dashboard" element={<PrivateRoute role="ADMIN"><AdminDashboard /></PrivateRoute>} />
            <Route path="/admin/search" element={<PrivateRoute role="ADMIN"><AdminSearch /></PrivateRoute>} />
            <Route path="/admin/customers" element={<PrivateRoute role="ADMIN"><AdminCustomers /></PrivateRoute>} />
            <Route path="/admin/accounts" element={<PrivateRoute role="ADMIN"><AdminAccounts /></PrivateRoute>} />
            <Route path="/admin/loans" element={<PrivateRoute role="ADMIN"><AdminLoans /></PrivateRoute>} />
            <Route path="/admin/transactions" element={<PrivateRoute role="ADMIN"><AdminTransactions /></PrivateRoute>} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
