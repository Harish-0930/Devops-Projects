import React, { useEffect, useState } from 'react';
import { getProfile } from '../../services/customerService';
import { getAccounts } from '../../services/accountService';
import { Link } from 'react-router-dom';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const CustomerDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const profile = await getProfile();
        const accounts = await getAccounts(profile.data.customerId);
        setData({ profile: profile.data, accounts: accounts.data });
      } catch (err) { console.error(err); }
      setLoading(false);
    };
    fetchData();
  }, []);

  if (loading) return <LoadingSpinner />;
  const totalBalance = data.accounts.reduce((sum, acc) => sum + parseFloat(acc.balance), 0);

  return (
    <div className="container py-4">
      <h2 className="mb-4">Welcome, {data.profile.firstName}!</h2>
      <div className="row g-4">
        <div className="col-md-4"><div className="dashboard-card"><h6>Total Balance</h6><h3>{formatCurrency(totalBalance)}</h3></div></div>
        <div className="col-md-4"><div className="dashboard-card" style={{background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)'}}><h6>Accounts</h6><h3>{data.accounts.length}</h3></div></div>
        <div className="col-md-4"><div className="dashboard-card" style={{background: 'linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%)'}}><h6>Customer ID</h6><h3>{data.profile.uniqueId}</h3></div></div>
      </div>
      <div className="card mt-4">
        <div className="card-body">
          <h5>Quick Actions</h5>
          <div className="d-flex gap-2 mt-3">
            <Link to="/accounts" className="btn btn-primary">View Accounts</Link>
            <Link to="/transactions" className="btn btn-info">View Transactions</Link>
            <Link to="/transfer" className="btn btn-success">Transfer Funds</Link>
            <Link to="/loans" className="btn btn-warning">Apply Loan</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomerDashboard;
