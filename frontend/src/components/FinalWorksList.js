import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { finalWorksAPI } from '../services/api';
import { getAverageRating } from '../utils/ratings';
import './FinalWorksList.css';

const FinalWorksList = () => {
  const [finalWorks, setFinalWorks] = useState([]);
  const [filteredWorks, setFilteredWorks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFinalWorks();
  }, []);

  useEffect(() => {
    filterWorks();
  }, [searchQuery, finalWorks]);

  const fetchFinalWorks = async () => {
    try {
      const response = await finalWorksAPI.getAll();
      setFinalWorks(response.data);
      setFilteredWorks(response.data);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se načíst práce');
      setLoading(false);
      console.error(err);
    }
  };

  const filterWorks = () => {
    if (!searchQuery.trim()) {
      setFilteredWorks(finalWorks);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = finalWorks.filter(work => 
      work.title.toLowerCase().includes(query) ||
      (work.description && work.description.toLowerCase().includes(query)) ||
      (work.studentName && work.studentName.toLowerCase().includes(query))
    );
    setFilteredWorks(filtered);
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  if (loading) return <div className="loading">Načítání...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="final-works-list">
      <div className="list-header">
        <h2>Všechny práce</h2>
        <div className="search-container">
          <input
            type="text"
            placeholder="Hledat podle názvu, popisu nebo autora..."
            value={searchQuery}
            onChange={handleSearchChange}
            className="search-input"
          />
          <span className="search-icon">🔍</span>
        </div>
      </div>

      {filteredWorks.length === 0 ? (
        <div className="empty-state">
          {searchQuery ? 'Nebyly nalezeny žádné práce odpovídající vašemu vyhledávání.' : 'Zatím nebyly přidány žádné práce.'}
        </div>
      ) : (
        <>
          {searchQuery && (
            <div className="search-results-info">
              Nalezeno {filteredWorks.length} {filteredWorks.length === 1 ? 'práce' : 'prací'}
            </div>
          )}
          <div className="works-grid">
            {filteredWorks.map((work) => (
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

export default FinalWorksList;

