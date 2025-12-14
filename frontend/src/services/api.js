import axios from 'axios';

const API_BASE_URL = 'https://localhost:8443/api';

// Create axios instance with default config
// Note: Browser will show SSL warning for self-signed certificate
// User needs to accept the certificate warning in the browser
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

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

