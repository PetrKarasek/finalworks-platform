import React, { useState, useEffect } from 'react';
import { finalWorksAPI, studentsAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './AdminPanel.css';

const AdminPanel = () => {
  const { isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('works');
  const [works, setWorks] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAdmin) {
      return;
    }
    fetchData();
  }, [isAdmin, activeTab]);

  const fetchData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'works') {
        const response = await finalWorksAPI.getAll();
        setWorks(response.data);
      } else {
        const response = await studentsAPI.getAll();
        setStudents(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch data', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteWork = async (id) => {
    if (!window.confirm('Opravdu chcete smazat tuto práci?')) {
      return;
    }

    try {
      await finalWorksAPI.delete(id);
      fetchData();
    } catch (err) {
      alert('Nepodařilo se smazat práci');
      console.error(err);
    }
  };

  const handleDeleteStudent = async (id) => {
    if (!window.confirm('Opravdu chcete smazat tohoto uživatele?')) {
      return;
    }

    try {
      await studentsAPI.delete(id);
      fetchData();
    } catch (err) {
      alert('Nepodařilo se smazat uživatele');
      console.error(err);
    }
  };

  if (!isAdmin) {
    return (
      <div className="admin-panel">
        <div className="access-denied">
          <h2>Přístup zamítnut</h2>
          <p>Nemáte oprávnění k přístupu do administračního panelu.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-panel">
      <h1>Administrační panel</h1>
      
      <div className="admin-tabs">
        <button
          className={activeTab === 'works' ? 'active' : ''}
          onClick={() => setActiveTab('works')}
        >
          Správa prací
        </button>
        <button
          className={activeTab === 'students' ? 'active' : ''}
          onClick={() => setActiveTab('students')}
        >
          Správa uživatelů
        </button>
      </div>

      {loading ? (
        <div className="loading">Načítání...</div>
      ) : (
        <>
          {activeTab === 'works' && (
            <div className="admin-content">
              <h2>Všechny práce ({works.length})</h2>
              {works.length === 0 ? (
                <div className="empty-state">Žádné práce</div>
              ) : (
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Název</th>
                        <th>Autor</th>
                        <th>Datum</th>
                        <th>Komentáře</th>
                        <th>Akce</th>
                      </tr>
                    </thead>
                    <tbody>
                      {works.map((work) => (
                        <tr key={work.id}>
                          <td>{work.id}</td>
                          <td>
                            <a href={`/works/${work.id}`} target="_blank" rel="noopener noreferrer">
                              {work.title}
                            </a>
                          </td>
                          <td>{work.studentName}</td>
                          <td>{new Date(work.submittedAt).toLocaleDateString('cs-CZ')}</td>
                          <td>{work.comments?.length || 0}</td>
                          <td>
                            <button
                              onClick={() => handleDeleteWork(work.id)}
                              className="delete-btn"
                            >
                              Smazat
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeTab === 'students' && (
            <div className="admin-content">
              <h2>Všichni uživatelé ({students.length})</h2>
              {students.length === 0 ? (
                <div className="empty-state">Žádní uživatelé</div>
              ) : (
                <div className="admin-table">
                  <table>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Jméno</th>
                        <th>Email</th>
                        <th>Akce</th>
                      </tr>
                    </thead>
                    <tbody>
                      {students.map((student) => (
                        <tr key={student.id}>
                          <td>{student.id}</td>
                          <td>{student.name}</td>
                          <td>{student.email}</td>
                          <td>
                            <button
                              onClick={() => handleDeleteStudent(student.id)}
                              className="delete-btn"
                            >
                              Smazat
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default AdminPanel;

