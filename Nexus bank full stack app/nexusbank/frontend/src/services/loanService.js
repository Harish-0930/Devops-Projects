import api from './api';

export const applyLoan = (data) => api.post('/api/loans/apply', data);
export const getLoans = (customerId) => api.get(`/api/loans/customer/${customerId}`);
export const getLoanStatus = (loanId) => api.get(`/api/loans/status/${loanId}`);