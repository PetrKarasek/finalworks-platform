import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || (process.env.NODE_ENV === 'development' ? 'https://localhost:8443/api' : '/api');

// Create axios instance with default config
// Note: Browser will show SSL warning for self-signed certificate
// User needs to accept the certificate warning in the browser
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('role');
    }
    return Promise.reject(error);
  }
);

// Final Works API
export const finalWorksAPI = {
  getAll: () => api.get('/final-works'),
  getById: (id) => api.get(`/final-works/${id}`),
  create: (data) => api.post('/final-works', data),
  update: (id, data) => api.put(`/final-works/${id}`, data),
  delete: (id) => api.delete(`/final-works/${id}`),
  getComments: (id) => api.get(`/final-works/${id}/comments`),
  addComment: (id, data) => api.post(`/final-works/${id}/comments`, data),
  deleteComment: (commentId) => api.delete(`/final-works/comments/${commentId}`),
};

// Students API
export const studentsAPI = {
  getAll: () => api.get('/students'),
  getById: (id) => api.get(`/students/${id}`),
  create: (data) => api.post('/students', data),
  update: (id, data) => api.put(`/students/${id}`, data),
  delete: (id) => api.delete(`/students/${id}`),
};

export default api;

