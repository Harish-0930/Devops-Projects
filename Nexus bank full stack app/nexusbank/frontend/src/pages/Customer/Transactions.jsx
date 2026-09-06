import React, { useState } from 'react';
import { getHistory, getMiniStatement } from '../../services/transactionService';
import { formatCurrency } from '../../utils/helpers';

const CustomerTransactions = () => {
  const [accountNumber, setAccountNumber] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [view, setView] = useState('history');

  const fetchData = async () => {
    if (!accountNumber) return;
    try {
      const res = view === 'history' ? await getHistory(accountNumber) : await getMiniStatement(accountNumber);
      setTransactions(res.data);
    } catch (err) { alert('Failed to fetch transactions'); }
  };

  return (
    <div className="container py-4">
      <h2 className="mb-4">Transaction History</h2>
      <div className="row mb-4">
        <div className="col-md-6">
          <input type="text" className="form-control" placeholder="Enter Account Number" value={accountNumber} onChange={e => setAccountNumber(e.target.value)} />
        </div>
        <div className="col-md-3">
          <select className="form-select" value={view} onChange={e => setView(e.target.value)}>
            <option value="history">Full History</option>
            <option value="mini">Mini Statement</option>
          </select>
        </div>
        <div className="col-md-3"><button className="btn btn-primary w-100" onClick={fetchData}>Fetch</button></div>
      </div>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Type</th>
              <th>From Account</th>
              <th>To Account</th>
              <th>Amount</th>
              <th>Balance After</th>
              <th>Date</th>
              <th>Remarks</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map(t => (
              <tr key={t.transactionId}>
                <td>{t.transactionId}</td>
                <td><span className={`badge bg-${t.transactionType === 'CREDIT' ? 'success' : 'danger'}`}>{t.transactionType}</span></td>
                <td>{t.fromAccountNumber || '-'}</td>
                <td>{t.toAccountNumber || '-'}</td>
                <td>{formatCurrency(t.amount)}</td>
                <td>{t.balanceAfter != null ? formatCurrency(t.balanceAfter) : '-'}</td>
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

export default CustomerTransactions;
