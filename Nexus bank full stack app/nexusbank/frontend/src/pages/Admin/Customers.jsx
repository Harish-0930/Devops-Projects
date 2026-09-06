import React, { useEffect, useState } from 'react';
import { getAllCustomers, activateCustomer, deactivateCustomer } from '../../services/adminService';
import LoadingSpinner from '../../components/LoadingSpinner';

const AdminCustomers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { fetchCustomers(); }, []);

  const fetchCustomers = () => {
    getAllCustomers().then(res => { setCustomers(res.data); setLoading(false); }).catch(console.error);
  };

  const toggleStatus = async (id, action) => {
    try {
      if (action === 'activate') await activateCustomer(id);
      else await deactivateCustomer(id);
      fetchCustomers();
    } catch (err) { alert('Action failed'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <h2 className="mb-4">All Customers</h2>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark"><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {customers.map(c => (
              <tr key={c.customerId}>
                <td>{c.uniqueId || c.customerId}</td>
                <td>{c.firstName} {c.lastName}</td>
                <td>{c.email}</td>
                <td>{c.phoneNumber}</td>
                <td><span className={`badge bg-${c.status === 'ACTIVE' ? 'success' : 'danger'}`}>{c.status}</span></td>
                <td>
                  {c.status === 'ACTIVE' ? 
                    <button className="btn btn-sm btn-warning" onClick={() => toggleStatus(c.customerId, 'deactivate')}>Deactivate</button> :
                    <button className="btn btn-sm btn-success" onClick={() => toggleStatus(c.customerId, 'activate')}>Activate</button>
                  }
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminCustomers;