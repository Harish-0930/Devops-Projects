import api from './api';

export const customerLogin = (credentials) => api.post('/api/customers/login', credentials);
export const customerRegister = (data) => api.post('/api/customers/register', data);
export const adminLogin = (credentials) => api.post('/api/admin/login', credentials);