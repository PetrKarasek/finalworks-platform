import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { finalWorksAPI } from '../services/api';
import { getAverageRating } from '../utils/ratings';
import './HomePage.css';

const HomePage = () => {
  const [works, setWorks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchWorks();
  }, []);

  const fetchWorks = async () => {
    try {
      const response = await finalWorksAPI.getAll();
      setWorks(response.data);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se načíst práce');
      setLoading(false);
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Načítání...</div>;
  if (error) return <div className="error">{error}</div>;

  // Sort by date (newest first)
  const newestWorks = [...works]
    .sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
    .slice(0, 6);

  // Sort by rating (best rated first)
  const bestRatedWorks = [...works]
    .map(work => ({
      ...work,
      rating: getAverageRating(work.id)
    }))
    .sort((a, b) => b.rating - a.rating)
    .slice(0, 6);

  const WorkCard = ({ work }) => (
    <Link to={`/works/${work.id}`} className="work-card">
      <h3>{work.title}</h3>
      <p className="student-name">Autor: {work.studentName}</p>
      <p className="description">{work.description || 'Bez popisu'}</p>
      <div className="work-meta">
        <span className="date">
          {new Date(work.submittedAt).toLocaleDateString('cs-CZ')}
        </span>
        <span className="comments-count">
          {work.comments?.length || 0} komentářů
        </span>
        {getAverageRating(work.id) > 0 && (
          <span className="rating">
            ⭐ {getAverageRating(work.id).toFixed(1)}
          </span>
        )}
      </div>
    </Link>
  );

  return (
    <div className="homepage">
      <section className="hero-section">
        <h1>Akademický portál</h1>
        <p className="hero-subtitle">
          Sdílejte a vyhledávejte studentské práce, odborné články a studijní materiály
        </p>
      </section>

      <section className="newest-section">
        <h2>Nejnovější příspěvky</h2>
        {newestWorks.length === 0 ? (
          <div className="empty-state">Zatím nebyly přidány žádné práce.</div>
        ) : (
          <div className="works-grid">
            {newestWorks.map((work) => (
              <WorkCard key={work.id} work={work} />
            ))}
          </div>
        )}
      </section>

      <section className="best-rated-section">
        <h2>Nejlépe hodnocené příspěvky</h2>
        {bestRatedWorks.length === 0 ? (
          <div className="empty-state">Zatím nebyly hodnoceny žádné práce.</div>
        ) : (
          <div className="works-grid">
            {bestRatedWorks.map((work) => (
              <WorkCard key={work.id} work={work} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default HomePage;

