import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || ''; // Don't pipe with localhost URL in production. In production, the REACT_APP_API_URL environment variable should be set to the API Gateway URL or Internal Load Balancer URL. In production Just use the empty string after pipe '' as the base URL, and the Nginx reverse proxy will handle routing to the appropriate backend service.

// const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080'; // Uncomment this line when running the frontend in development mode. In production, the REACT_APP_API_URL environment variable should be set to the API Gateway URL or Internal Load Balancer URL.

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;