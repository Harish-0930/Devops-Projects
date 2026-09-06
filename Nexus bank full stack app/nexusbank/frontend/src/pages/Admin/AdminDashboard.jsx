import React, { useEffect, useState } from 'react';
import { getDashboard } from '../../services/adminService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const AdminDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboard().then(res => { setData(res.data); setLoading(false); }).catch(console.error);
  }, []);

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <h2 className="mb-4">Admin Dashboard</h2>
      <div className="row g-4">
        <div className="col-md-3"><div className="dashboard-card"><h6>Total Customers</h6><h3>{data.totalCustomers}</h3></div></div>
        <div className="col-md-3"><div className="dashboard-card" style={{background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)'}}><h6>Total Accounts</h6><h3>{data.totalAccounts}</h3></div></div>
        <div className="col-md-3"><div className="dashboard-card" style={{background: 'linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%)'}}><h6>Total Transactions</h6><h3>{data.totalTransactions}</h3></div></div>
        <div className="col-md-3"><div className="dashboard-card" style={{background: 'linear-gradient(135deg, #8E2DE2 0%, #4A00E0 100%)'}}><h6>Total Loans</h6><h3>{data.totalLoans}</h3></div></div>
      </div>
      <div className="row g-4 mt-2">
        <div className="col-md-6"><div className="card p-3"><h5>Pending Account Approvals</h5><h2 className="text-warning">{data.pendingAccountApprovals}</h2></div></div>
        <div className="col-md-6"><div className="card p-3"><h5>Pending Loan Approvals</h5><h2 className="text-warning">{data.pendingLoanApprovals}</h2></div></div>
      </div>
    </div>
  );
};

export default AdminDashboard;