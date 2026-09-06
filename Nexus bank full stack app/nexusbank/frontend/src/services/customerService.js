import api from './api';

export const getProfile = () => api.get('/api/customers/profile');
export const updateProfile = (data) => api.put('/api/customers/update', data);
export const getAddress = () => api.get('/api/customers/address');
export const addAddress = (data) => api.post('/api/customers/address', data);