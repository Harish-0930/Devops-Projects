import React, { useEffect, useState } from 'react';
import { getAllTransactions } from '../../services/adminService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { formatCurrency } from '../../utils/helpers';

const AdminTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllTransactions().then(res => { setTransactions(res.data); setLoading(false); }).catch(console.error);
  }, []);

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <h2 className="mb-4">Transaction Monitoring</h2>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>ID</th><th>Type</th><th>Amount</th><th>From Account</th><th>To Account</th><th>Date</th><th>Remarks</th></tr></thead>
          <tbody>
            {transactions.map(t => (
              <tr key={t.transactionId}>
                <td>{t.transactionId}</td>
                <td><span className={`badge bg-${t.transactionType === 'CREDIT' ? 'success' : 'danger'}`}>{t.transactionType}</span></td>
                <td>{formatCurrency(t.amount)}</td>
                <td>{t.fromAccountNumber || '-'}</td>
                <td>{t.toAccountNumber || '-'}</td>
                <td>{new Date(t.transactionDate).toLocaleString()}</td>
                <td>{t.remarks || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminTransactions;
