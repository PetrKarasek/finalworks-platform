import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { finalWorksAPI, studentsAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './UploadWork.css';

const UploadWork = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    fileUrl: '',
    studentId: null
  });
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStudents();
    if (user) {
      setFormData(prev => ({ ...prev, studentId: user.id }));
    }
  }, [user]);

  const fetchStudents = async () => {
    try {
      const response = await studentsAPI.getAll();
      setStudents(response.data);
    } catch (err) {
      console.error('Failed to load students', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!formData.title.trim() || !formData.fileUrl.trim() || !formData.studentId) {
      setError('Prosím vyplňte všechna povinná pole');
      return;
    }

    setLoading(true);
    try {
      await finalWorksAPI.create(formData);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Nepodařilo se nahrát práci');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-work">
      <h2>Nahrát novou práci</h2>
      
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="upload-form">
        <div className="form-group">
          <label htmlFor="title">Název práce *</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            placeholder="Zadejte název práce"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Popis</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Zadejte popis práce"
            rows="5"
          />
        </div>

        <div className="form-group">
          <label htmlFor="fileUrl">URL souboru *</label>
          <input
            type="url"
            id="fileUrl"
            name="fileUrl"
            value={formData.fileUrl}
            onChange={handleChange}
            placeholder="https://example.com/document.pdf"
            required
          />
          <small>Zadejte URL odkazu na váš dokument</small>
        </div>

        <div className="form-group">
          <label htmlFor="studentId">Autor *</label>
          <select
            id="studentId"
            name="studentId"
            value={formData.studentId || ''}
            onChange={handleChange}
            required
            disabled={!!user}
          >
            <option value="">Vyberte autora</option>
            {students.map(student => (
              <option key={student.id} value={student.id}>
                {student.name} ({student.email})
              </option>
            ))}
          </select>
          {user && <small>Jste přihlášeni jako: {user.name}</small>}
        </div>

        <div className="form-actions">
          <button type="button" onClick={() => navigate('/')} className="btn-secondary">
            Zrušit
          </button>
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Nahrávání...' : 'Nahrát práci'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default UploadWork;

