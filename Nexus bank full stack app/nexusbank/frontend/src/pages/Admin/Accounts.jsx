import React, { useEffect, useState } from 'react';
import { getAllAccounts, approveAccount, rejectAccount, freezeAccount, unfreezeAccount } from '../../services/adminService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const AdminAccounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { fetchAccounts(); }, []);

  const fetchAccounts = () => {
    getAllAccounts().then(res => { setAccounts(res.data); setLoading(false); }).catch(console.error);
  };

  const handleAction = async (num, action) => {
    try {
      if (action === 'approve') await approveAccount(num);
      else if (action === 'reject') await rejectAccount(num);
      else if (action === 'freeze') await freezeAccount(num);
      else if (action === 'unfreeze') await unfreezeAccount(num);
      fetchAccounts();
    } catch (err) { alert('Action failed'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <h2 className="mb-4">All Accounts</h2>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>Account #</th><th>Type</th><th>Balance</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {accounts.map(a => (
              <tr key={a.accountNumber}>
                <td>{a.accountNumber}</td>
                <td>{a.accountType}</td>
                <td>{formatCurrency(a.balance)}</td>
                <td><span className={`badge bg-${a.accountStatus === 'ACTIVE' ? 'success' : a.accountStatus === 'PENDING' ? 'warning' : a.accountStatus === 'FROZEN' ? 'info' : 'danger'}`}>{a.accountStatus}</span></td>
                <td>
                  {a.accountStatus === 'PENDING' && <><button className="btn btn-sm btn-success me-1" onClick={() => handleAction(a.accountNumber, 'approve')}>Approve</button><button className="btn btn-sm btn-danger" onClick={() => handleAction(a.accountNumber, 'reject')}>Reject</button></>}
                  {a.accountStatus === 'ACTIVE' && <button className="btn btn-sm btn-info" onClick={() => handleAction(a.accountNumber, 'freeze')}>Freeze</button>}
                  {a.accountStatus === 'FROZEN' && <button className="btn btn-sm btn-success" onClick={() => handleAction(a.accountNumber, 'unfreeze')}>Unfreeze</button>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminAccounts;