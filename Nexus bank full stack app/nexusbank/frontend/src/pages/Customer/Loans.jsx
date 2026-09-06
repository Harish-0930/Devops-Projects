import React, { useEffect, useState } from 'react';
import { getProfile } from '../../services/customerService';
import { getLoans, applyLoan } from '../../services/loanService';
import { getAccounts } from '../../services/accountService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const CustomerLoans = () => {
  const interestRates = { PERSONAL: 18, HOME: 10, VEHICLE: 8, EDUCATION: 6, BUSINESS: 12 };
  const [loans, setLoans] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [newLoan, setNewLoan] = useState({ loanType: 'PERSONAL', amount: '', tenure: '', interestRate: interestRates.PERSONAL });

  useEffect(() => { fetchLoans(); }, []);

  const fetchLoans = async () => {
    try {
      const profile = await getProfile();
      const res = await getLoans(profile.data.customerId);
      setLoans(res.data);
      const accountRes = await getAccounts(profile.data.customerId);
      setAccounts(accountRes.data);
    } catch (err) { console.error(err); }
    setLoading(false);
  };

  const handleApply = async (e) => {
    e.preventDefault();
    try {
      await applyLoan({ ...newLoan, amount: parseFloat(newLoan.amount), tenure: parseInt(newLoan.tenure) });
      setShowForm(false);
      fetchLoans();
    } catch (err) { alert('Failed to apply'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>My Loans</h2>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>+ Apply Loan</button>
      </div>
      {showForm && (
        <div className="card p-3 mb-4">
          <form onSubmit={handleApply}>
            <div className="row">
              <div className="col-md-2"><label>Account</label><select className="form-select" value={newLoan.accountNumber || ''} onChange={e => setNewLoan({...newLoan, accountNumber: e.target.value})} required><option value="">Select active account</option>{accounts.filter(account => account.accountStatus === 'ACTIVE').map(account => <option key={account.accountNumber} value={account.accountNumber}>{account.accountNumber}</option>)}</select></div>
              <div className="col-md-2"><label>Type</label><select className="form-select" value={newLoan.loanType} onChange={e => setNewLoan({...newLoan, loanType: e.target.value, interestRate: interestRates[e.target.value]})}><option value="PERSONAL">Personal</option><option value="HOME">Home</option><option value="VEHICLE">Vehicle</option><option value="EDUCATION">Education</option><option value="BUSINESS">Business</option></select></div>
              <div className="col-md-2"><label>Amount</label><input type="number" min="0.01" step="0.01" className="form-control" value={newLoan.amount} onChange={e => setNewLoan({...newLoan, amount: e.target.value})} required /></div>
              <div className="col-md-2"><label>Tenure (months)</label><input type="number" min="1" className="form-control" value={newLoan.tenure} onChange={e => setNewLoan({...newLoan, tenure: e.target.value})} required /></div>
              <div className="col-md-2"><label>Interest %</label><input type="number" className="form-control" value={newLoan.interestRate} readOnly aria-label="Bank-defined interest rate" /></div>
              <div className="col-md-2 d-flex align-items-end"><button type="submit" className="btn btn-success">Apply</button></div>
            </div>
          </form>
        </div>
      )}
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>ID</th><th>Account</th><th>Type</th><th>Amount</th><th>Tenure</th><th>Rate</th><th>Status</th><th>Applied</th></tr></thead>
          <tbody>
            {loans.map(l => (
              <tr key={l.loanId}>
                <td>#{l.loanId}</td>
                <td>{l.accountNumber}</td>
                <td>{l.loanType}</td>
                <td>{formatCurrency(l.amount)}</td>
                <td>{l.tenure} months</td>
                <td>{l.interestRate}%</td>
                <td><span className={`badge bg-${l.loanStatus === 'APPROVED' ? 'success' : l.loanStatus === 'PENDING' ? 'warning' : 'danger'}`}>{l.loanStatus}</span></td>
                <td>{new Date(l.appliedDate).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerLoans;
