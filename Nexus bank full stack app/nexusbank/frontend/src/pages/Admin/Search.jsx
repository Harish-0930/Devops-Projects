import React, { useState } from 'react';
import { getAccountByNumber, getAccountsByCustomer, getAllCustomers, getLoansByAccount, getTransactionsByAccount } from '../../services/adminService';
import { formatCurrency } from '../../utils/helpers';

const AdminSearch = () => {
  const [mode, setMode] = useState('customer');
  const [query, setQuery] = useState('');
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loans, setLoans] = useState([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const search = async (event) => {
    event.preventDefault();
    const value = query.trim();
    if (!value) return;
    setLoading(true); setMessage(''); setAccounts([]); setTransactions([]); setLoans([]);
    try {
      if (mode === 'customer') {
        // Supports the displayed unique ID as well as the internal numeric ID.
        const customers = (await getAllCustomers()).data;
        const customer = customers.find(c => c.uniqueId === value || String(c.customerId) === value);
        if (!customer) throw new Error('Customer not found');
        setAccounts((await getAccountsByCustomer(customer.customerId)).data);
      } else {
        const account = (await getAccountByNumber(value)).data;
        const [transactionResult, loanResult] = await Promise.all([
          getTransactionsByAccount(account.accountNumber),
          getLoansByAccount(account.accountNumber)
        ]);
        setAccounts([account]);
        setTransactions(transactionResult.data);
        setLoans(loanResult.data);
      }
    } catch (err) {
      setMessage(mode === 'customer' ? 'No customer matches that ID.' : 'No account matches that account number.');
    } finally { setLoading(false); }
  };

  return <div className="container py-4">
    <h2 className="mb-4">Customer & Account Search</h2>
    <form className="row g-2 mb-4" onSubmit={search}>
      <div className="col-md-3"><select className="form-select" value={mode} onChange={e => setMode(e.target.value)}><option value="customer">Customer ID</option><option value="account">Account Number</option></select></div>
      <div className="col-md-6"><input className="form-control" value={query} onChange={e => setQuery(e.target.value)} placeholder={mode === 'customer' ? 'Enter customer ID' : 'Enter account number'} /></div>
      <div className="col-md-3"><button className="btn btn-primary w-100" disabled={loading}>{loading ? 'Searching...' : 'Search'}</button></div>
    </form>
    {message && <div className="alert alert-warning">{message}</div>}
    {accounts.length > 0 && <section className="mb-4"><h4>Accounts</h4><div className="table-responsive"><table className="table table-hover"><thead className="table-dark"><tr><th>Account #</th><th>Type</th><th>Balance</th><th>Status</th></tr></thead><tbody>{accounts.map(a => <tr key={a.accountNumber}><td>{a.accountNumber}</td><td>{a.accountType}</td><td>{formatCurrency(a.balance)}</td><td>{a.accountStatus}</td></tr>)}</tbody></table></div></section>}
    {mode === 'account' && <section className="mb-4"><h4>Transactions</h4><div className="table-responsive"><table className="table table-hover"><thead className="table-dark"><tr><th>ID</th><th>Type</th><th>From Account</th><th>To Account</th><th>Amount</th><th>Balance After</th><th>Date</th></tr></thead><tbody>{transactions.map(t => <tr key={t.transactionId}><td>{t.transactionId}</td><td>{t.transactionType}</td><td>{t.fromAccountNumber || '-'}</td><td>{t.toAccountNumber || '-'}</td><td>{formatCurrency(t.amount)}</td><td>{t.balanceAfter == null ? '-' : formatCurrency(t.balanceAfter)}</td><td>{new Date(t.transactionDate).toLocaleString()}</td></tr>)}</tbody></table></div>{transactions.length === 0 && <p className="text-muted">No transactions found for this account.</p>}</section>}
    {mode === 'account' && <section><h4>Loans</h4><p className="text-muted">Loans linked to this account.</p><div className="table-responsive"><table className="table table-hover"><thead className="table-dark"><tr><th>ID</th><th>Type</th><th>Amount</th><th>Tenure</th><th>Rate</th><th>Status</th></tr></thead><tbody>{loans.map(l => <tr key={l.loanId}><td>{l.loanId}</td><td>{l.loanType}</td><td>{formatCurrency(l.amount)}</td><td>{l.tenure} months</td><td>{l.interestRate}%</td><td>{l.loanStatus}</td></tr>)}</tbody></table></div>{loans.length === 0 && <p className="text-muted">No loans found for this account.</p>}</section>}
  </div>;
};

export default AdminSearch;
