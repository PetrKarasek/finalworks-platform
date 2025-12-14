import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { studentsAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './LoginPage.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});

  const validatePassword = (password) => {
    const errors = [];
    if (password.length < 8) {
      errors.push('Heslo musí mít alespoň 8 znaků');
    }
    if (!/[A-Z]/.test(password)) {
      errors.push('Heslo musí obsahovat alespoň jedno velké písmeno');
    }
    return errors;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError(null);
    
    // Real-time password validation
    if (name === 'password' && !isLogin) {
      const passwordErrors = validatePassword(value);
      setValidationErrors(prev => ({
        ...prev,
        password: passwordErrors
      }));
    } else if (name === 'password') {
      setValidationErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors.password;
        return newErrors;
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (isLogin) {
        // Simple login - in a real app, this would be an authentication endpoint
        // For now, we'll just check if user exists and set them as logged in
        const response = await studentsAPI.getAll();
        const user = response.data.find(s => s.email === formData.email);
        
        if (user) {
          login(user, formData.email === 'admin@example.com'); // Simple admin check
          navigate('/');
        } else {
          setError('Uživatel s tímto emailem nebyl nalezen');
        }
      } else {
        // Register
        const response = await studentsAPI.create({
          name: formData.name,
          email: formData.email,
          password: formData.password
        });
        login(response.data, false);
        navigate('/');
      }
    } catch (err) {
      // Handle validation errors from backend
      if (err.response?.data?.fieldErrors) {
        const fieldErrors = err.response.data.fieldErrors;
        setValidationErrors(fieldErrors);
        setError('Prosím opravte chyby ve formuláři');
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Něco se pokazilo. Zkuste to prosím znovu.');
      }
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <h2>{isLogin ? 'Přihlášení' : 'Registrace'}</h2>
        
        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="name">Jméno</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Zadejte vaše jméno"
                required
              />
            </div>
          )}

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="vas@email.cz"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Heslo</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder={isLogin ? "Zadejte heslo" : "Min. 8 znaků, 1 velké písmeno"}
              required
              className={validationErrors.password ? 'error-input' : ''}
            />
            {!isLogin && validationErrors.password && (
              <div className="validation-errors">
                {Array.isArray(validationErrors.password) ? (
                  validationErrors.password.map((err, idx) => (
                    <div key={idx} className="validation-error">{err}</div>
                  ))
                ) : (
                  <div className="validation-error">{validationErrors.password}</div>
                )}
              </div>
            )}
            {!isLogin && formData.password && !validationErrors.password && (
              <div className="validation-success">✓ Heslo splňuje požadavky</div>
            )}
          </div>

          <button type="submit" disabled={loading} className="submit-btn">
            {loading ? 'Zpracování...' : (isLogin ? 'Přihlásit se' : 'Registrovat se')}
          </button>
        </form>

        <div className="form-switch">
          <p>
            {isLogin ? 'Nemáte účet? ' : 'Již máte účet? '}
            <button
              type="button"
              onClick={() => {
                setIsLogin(!isLogin);
                setError(null);
                setFormData({ name: '', email: '', password: '' });
              }}
              className="switch-btn"
            >
              {isLogin ? 'Registrovat se' : 'Přihlásit se'}
            </button>
          </p>
        </div>

        <div className="demo-info">
          <p><strong>Demo:</strong> Pro přihlášení jako admin použijte email: admin@example.com</p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;

