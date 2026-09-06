import React, { useEffect, useState } from 'react';
import { getProfile } from '../../services/customerService';
import { getAccounts, createAccount } from '../../services/accountService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const CustomerAccounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [newAccount, setNewAccount] = useState({ accountType: 'SAVINGS', balance: 0 });

  useEffect(() => { fetchAccounts(); }, []);

  const fetchAccounts = async () => {
    try {
      const profile = await getProfile();
      const res = await getAccounts(profile.data.customerId);
      setAccounts(res.data);
    } catch (err) { console.error(err); }
    setLoading(false);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await createAccount(newAccount);
      setShowForm(false);
      fetchAccounts();
    } catch (err) { alert('Failed to create account'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>My Accounts</h2>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>+ New Account</button>
      </div>
      {showForm && (
        <div className="card p-3 mb-4">
          <form onSubmit={handleCreate}>
            <div className="row">
              <div className="col-md-4"><label>Account Type</label><select className="form-select" value={newAccount.accountType} onChange={e => setNewAccount({...newAccount, accountType: e.target.value})}><option value="SAVINGS">Savings</option><option value="CURRENT">Current</option></select></div>
              <div className="col-md-4"><label>Initial Balance</label><input type="number" min="0" step="0.01" className="form-control" value={newAccount.balance} onChange={e => setNewAccount({...newAccount, balance: e.target.value})} required /></div>
              <div className="col-md-4 d-flex align-items-end"><button type="submit" className="btn btn-success">Create</button></div>
            </div>
          </form>
        </div>
      )}
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>Account Number</th><th>Type</th><th>Balance</th><th>Status</th><th>Created</th></tr></thead>
          <tbody>
            {accounts.map(acc => (
              <tr key={acc.accountNumber}>
                <td>{acc.accountNumber}</td>
                <td>{acc.accountType}</td>
                <td>{formatCurrency(acc.balance)}</td>
                <td><span className={`badge bg-${acc.accountStatus === 'ACTIVE' ? 'success' : acc.accountStatus === 'PENDING' ? 'warning' : 'danger'}`}>{acc.accountStatus}</span></td>
                <td>{new Date(acc.createdDate).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerAccounts;
