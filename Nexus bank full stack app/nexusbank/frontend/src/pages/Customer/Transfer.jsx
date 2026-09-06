import React, { useState, useEffect } from 'react';
import { transfer } from '../../services/transactionService';
import { getAccounts } from '../../services/accountService';
import { useAuth } from '../../context/AuthContext';

const Transfer = () => {
  const [form, setForm] = useState({ sourceAccountNumber: '', destinationAccountNumber: '', amount: '', remarks: '' });
  const [message, setMessage] = useState('');
  const [accounts, setAccounts] = useState([]);
  const { user } = useAuth();

  useEffect(() => {
    const load = async () => {
      if (!user?.customerId) return;
      try {
        const res = await getAccounts(user.customerId);
        setAccounts(res.data || []);
        if (res.data && res.data.length) setForm(f => ({ ...f, sourceAccountNumber: res.data[0].accountNumber }));
      } catch (err) { console.error(err); }
    };
    load();
  }, [user]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await transfer({ ...form, amount: parseFloat(form.amount) });
      setMessage('Transfer successful!');
      setForm({ sourceAccountNumber: '', destinationAccountNumber: '', amount: '', remarks: '' });
    } catch (err) { setMessage(err.response?.data?.message || 'Transfer failed'); }
  };

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card p-4">
            <h3 className="text-center mb-4">Fund Transfer</h3>
            {message && <div className={`alert alert-${message.includes('successful') ? 'success' : 'danger'}`}>{message}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label>From Account</label>
                <select className="form-select" value={form.sourceAccountNumber} onChange={e => setForm({...form, sourceAccountNumber: e.target.value})} required>
                  <option value="">Select account</option>
                  {accounts.map(a => <option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber} — {a.accountType}</option>)}
                </select>
              </div>
              <div className="mb-3"><label>To Account</label><input className="form-control" value={form.destinationAccountNumber} onChange={e => setForm({...form, destinationAccountNumber: e.target.value})} required /></div>
              <div className="mb-3"><label>Amount</label><input type="number" min="0.01" step="0.01" className="form-control" value={form.amount} onChange={e => setForm({...form, amount: e.target.value})} required /></div>
              <div className="mb-3"><label>Remarks</label><input className="form-control" value={form.remarks} onChange={e => setForm({...form, remarks: e.target.value})} /></div>
              <button type="submit" className="btn btn-primary w-100">Transfer</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Transfer;
