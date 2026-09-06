import React, { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../../services/customerService';
import LoadingSpinner from '../../components/LoadingSpinner';

const CustomerProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    getProfile().then(res => { setProfile(res.data); setLoading(false); }).catch(console.error);
  }, []);

  const handleSave = async () => {
    try {
      await updateProfile(profile);
      setEditing(false);
    } catch (err) { alert('Update failed'); }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="container py-4">
      <div className="card p-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3>My Profile</h3>
          <button className="btn btn-primary" onClick={() => editing ? handleSave() : setEditing(true)}>{editing ? 'Save' : 'Edit'}</button>
        </div>
        <div className="row">
          <div className="col-md-6 mb-3"><label>First Name</label><input className="form-control" value={profile.firstName} onChange={e => setProfile({...profile, firstName: e.target.value})} disabled={!editing} /></div>
          <div className="col-md-6 mb-3"><label>Last Name</label><input className="form-control" value={profile.lastName} onChange={e => setProfile({...profile, lastName: e.target.value})} disabled={!editing} /></div>
          <div className="col-md-6 mb-3"><label>Email</label><input className="form-control" value={profile.email} disabled /></div>
          <div className="col-md-6 mb-3"><label>Phone</label><input className="form-control" value={profile.phoneNumber} onChange={e => setProfile({...profile, phoneNumber: e.target.value})} disabled={!editing} /></div>
          <div className="col-md-6 mb-3"><label>Username</label><input className="form-control" value={profile.username} disabled /></div>
          <div className="col-md-6 mb-3"><label>Status</label><input className="form-control" value={profile.status} disabled /></div>
        </div>
        {profile.address && (
          <>
            <h5 className="mt-3">Address</h5>
            <div className="row">
              <div className="col-12 mb-2"><label>Street</label><input className="form-control" value={profile.address.street} disabled /></div>
              <div className="col-md-4 mb-2"><label>City</label><input className="form-control" value={profile.address.city} disabled /></div>
              <div className="col-md-4 mb-2"><label>State</label><input className="form-control" value={profile.address.state} disabled /></div>
              <div className="col-md-4 mb-2"><label>Pincode</label><input className="form-control" value={profile.address.pincode} disabled /></div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default CustomerProfile;