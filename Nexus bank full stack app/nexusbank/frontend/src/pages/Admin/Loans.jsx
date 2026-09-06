import React, { useEffect, useState } from 'react';
import { getAllLoans, getPendingLoans, approveLoan, rejectLoan } from '../../services/adminService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const AdminLoans = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');

  useEffect(() => { fetchLoans(); }, [filter]);

  const fetchLoans = () => {
    const api = filter === 'pending' ? getPendingLoans() : getAllLoans();
    api.then(res => { setLoans(res.data); setLoading(false); }).catch(console.error);
  };

  const handleAction = async (id, action) => {
    try {
      if (action === 'approve') await approveLoan(id);
      else await rejectLoan(id);
      fetchLoans();
    } catch (err) { alert('Action failed'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Loan Management</h2>
        <select className="form-select w-auto" value={filter} onChange={e => setFilter(e.target.value)}>
          <option value="all">All Loans</option>
          <option value="pending">Pending Only</option>
        </select>
      </div>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>ID</th><th>Type</th><th>Amount</th><th>Tenure</th><th>Rate</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {loans.map(l => (
              <tr key={l.loanId}>
                <td>#{l.loanId}</td>
                <td>{l.loanType}</td>
                <td>{formatCurrency(l.amount)}</td>
                <td>{l.tenure} months</td>
                <td>{l.interestRate}%</td>
                <td><span className={`badge bg-${l.loanStatus === 'APPROVED' ? 'success' : l.loanStatus === 'PENDING' ? 'warning' : 'danger'}`}>{l.loanStatus}</span></td>
                <td>
                  {l.loanStatus === 'PENDING' && <><button className="btn btn-sm btn-success me-1" onClick={() => handleAction(l.loanId, 'approve')}>Approve</button><button className="btn btn-sm btn-danger" onClick={() => handleAction(l.loanId, 'reject')}>Reject</button></>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminLoans;