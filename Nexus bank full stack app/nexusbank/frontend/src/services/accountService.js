import api from './api';

export const createAccount = (data) => api.post('/api/accounts/create', data);
export const getAccounts = (customerId) => api.get(`/api/accounts/customer/${customerId}`);
export const getAccount = (accountNumber) => api.get(`/api/accounts/${accountNumber}`);