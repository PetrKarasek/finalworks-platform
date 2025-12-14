import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { finalWorksAPI } from '../services/api';
import { getBookmarkedWorks } from '../utils/bookmarks';
import { getAverageRating } from '../utils/ratings';
import { useAuth } from '../context/AuthContext';
import './BookmarksPage.css';

const BookmarksPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [bookmarkedWorks, setBookmarkedWorks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    fetchBookmarkedWorks();
  }, [isAuthenticated, navigate]);

  const fetchBookmarkedWorks = async () => {
    try {
      const response = await finalWorksAPI.getAll();
      const allWorks = response.data;
      const bookmarked = getBookmarkedWorks(allWorks);
      setBookmarkedWorks(bookmarked);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se načíst záložky');
      setLoading(false);
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Načítání...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="bookmarks-page">
      <h1>Moje záložky</h1>
      
      {bookmarkedWorks.length === 0 ? (
        <div className="empty-state">
          <p>Zatím nemáte žádné uložené záložky.</p>
          <Link to="/" className="browse-link">Procházet práce</Link>
        </div>
      ) : (
        <>
          <p className="bookmarks-count">Máte {bookmarkedWorks.length} {bookmarkedWorks.length === 1 ? 'uloženou záložku' : 'uložených záložek'}</p>
          <div className="works-grid">
            {bookmarkedWorks.map((work) => (
              <Link key={work.id} to={`/works/${work.id}`} className="work-card">
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
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default BookmarksPage;

