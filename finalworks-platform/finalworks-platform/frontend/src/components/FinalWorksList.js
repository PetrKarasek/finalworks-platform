import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './FinalWorksList.css';

const FinalWorksList = () => {
  const [finalWorks, setFinalWorks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFinalWorks();
  }, []);

  const fetchFinalWorks = async () => {
    try {
      const response = await axios.get('https://localhost:8443/api/final-works');
      setFinalWorks(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load final works');
      setLoading(false);
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="final-works-list">
      <h2>Student Final Works</h2>
      {finalWorks.length === 0 ? (
        <div className="empty-state">No final works submitted yet.</div>
      ) : (
        <div className="works-grid">
          {finalWorks.map((work) => (
            <Link key={work.id} to={`/works/${work.id}`} className="work-card">
              <h3>{work.title}</h3>
              <p className="student-name">By: {work.studentName}</p>
              <p className="description">{work.description || 'No description'}</p>
              <div className="work-meta">
                <span className="date">
                  {new Date(work.submittedAt).toLocaleDateString()}
                </span>
                <span className="comments-count">
                  {work.comments?.length || 0} comments
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default FinalWorksList;

