import api from './api';

export const deposit = (data) => api.post('/api/transactions/deposit', data);
export const withdraw = (data) => api.post('/api/transactions/withdraw', data);
export const transfer = (data) => api.post('/api/transactions/transfer', data);
export const getHistory = (accountNumber) => api.get(`/api/transactions/history/${accountNumber}`);
export const getMiniStatement = (accountNumber) => api.get(`/api/transactions/mini-statement/${accountNumber}`);