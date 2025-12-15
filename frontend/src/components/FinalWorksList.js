import React, { useState, useEffect, useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { finalWorksAPI, tagsAPI } from '../services/api';
import './FinalWorksList.css';

const FinalWorksList = () => {
  const [searchParams] = useSearchParams();
  const [finalWorks, setFinalWorks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [view, setView] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTags, setSelectedTags] = useState([]);
  const [availableTags, setAvailableTags] = useState([]);

  const fetchFinalWorks = useCallback(async (endpoint) => {
    try {
      const response = await finalWorksAPI[endpoint]();
      setFinalWorks(response.data);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se načíst práce');
      setLoading(false);
    }
  }, []);

  const fetchTags = useCallback(async () => {
    try {
      const response = await tagsAPI.getPopular();
      setAvailableTags(response.data);
    } catch (err) {
      console.error('Failed to fetch tags:', err);
    }
  }, []);

  const handleSearch = useCallback(async () => {
    if (!searchQuery.trim()) {
      fetchFinalWorks('getAll');
      return;
    }
    try {
      const response = await finalWorksAPI.search(searchQuery);
      setFinalWorks(response.data);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se vyhledat práce');
      setLoading(false);
    }
  }, [searchQuery, fetchFinalWorks]);

  const handleFilterByTags = useCallback(async () => {
    console.log('handleFilterByTags called with selectedTags:', selectedTags);
    if (selectedTags.length === 0) {
      console.log('No tags selected, fetching all works');
      fetchFinalWorks('getAll');
      return;
    }
    try {
      console.log('Calling API filterByTags with:', selectedTags);
      const response = await finalWorksAPI.filterByTags(selectedTags);
      console.log('API response:', response.data);
      setFinalWorks(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error in handleFilterByTags:', err);
      setError('Nepodařilo se filtrovat práce');
      setLoading(false);
    }
  }, [selectedTags, fetchFinalWorks]);

  useEffect(() => {
    // Read URL parameters on component mount
    const tagsParam = searchParams.get('tags');
    const queryParam = searchParams.get('search');
    
    console.log('URL params read - tagsParam:', tagsParam, 'queryParam:', queryParam);
    
    if (tagsParam) {
      console.log('Setting selectedTags from URL:', [tagsParam]);
      setSelectedTags([tagsParam]);
    }
    if (queryParam) {
      console.log('Setting searchQuery from URL:', queryParam);
      setSearchQuery(queryParam);
    }
  }, [searchParams]);

  useEffect(() => {
    console.log('Fetch useEffect triggered - view:', view, 'searchParams:', searchParams, 'fetchFinalWorks:', fetchFinalWorks, 'fetchTags:', fetchTags, 'handleSearch:', handleSearch, 'handleFilterByTags:', handleFilterByTags);
    // Only fetch after URL parameters have been processed
    const tagsParam = searchParams.get('tags');
    const queryParam = searchParams.get('search');
    
    console.log('Fetch useEffect triggered - tagsParam:', tagsParam, 'selectedTags:', selectedTags);
    
    if (queryParam) {
      console.log('Fetching by search query');
      handleSearch();
    } else if (tagsParam) {
      console.log('Fetching by tags from URL');
      handleFilterByTags();
    } else if (selectedTags.length > 0) {
      console.log('Fetching by selectedTags state');
      handleFilterByTags();
    } else {
      console.log('Fetching all works');
      if (view === 'newest') fetchFinalWorks('getNewest');
      else if (view === 'top-rated') fetchFinalWorks('getTopRated');
      else fetchFinalWorks('getAll');
    }
    fetchTags();
  }, [view, searchParams, selectedTags, fetchFinalWorks, fetchTags, handleSearch, handleFilterByTags]);

  const toggleTag = (tagName) => {
    console.log('Toggle tag called:', tagName);
    setSelectedTags(prevSelectedTags => {
      console.log('Current selectedTags:', prevSelectedTags);
      const newTags = prevSelectedTags.includes(tagName)
        ? prevSelectedTags.filter(t => t !== tagName)
        : [...prevSelectedTags, tagName];
      console.log('New selectedTags:', newTags);
      return newTags;
    });
  };

  if (loading) return <div className="loading">Načítání...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="final-works-list">
      <div className="list-header">
        <h2>Práce</h2>
        <div className="view-controls">
          <button className={view === 'all' ? 'active' : ''} onClick={() => setView('all')}>Všechny</button>
          <button className={view === 'newest' ? 'active' : ''} onClick={() => setView('newest')}>Nejnovější</button>
          <button className={view === 'top-rated' ? 'active' : ''} onClick={() => setView('top-rated')}>Nejlepší</button>
        </div>
      </div>

      <div className="filters">
        <div className="search-container">
          <input
            type="text"
            placeholder="Hledat podle názvu..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          <span className="search-icon">🔍</span>
        </div>
        
        <div className="tag-filter">
          <span>Tagy:</span>
          {availableTags.map(tag => (
            <button
              key={tag.id}
              className={`tag-button ${selectedTags.includes(tag.name) ? 'selected' : ''}`}
              onClick={() => toggleTag(tag.name)}
            >
              {tag.name}
            </button>
          ))}
        </div>
      </div>

      {finalWorks.length === 0 ? (
        <div className="empty-state">
          {searchQuery || selectedTags.length > 0 
            ? 'Nebyly nalezeny žádné práce odpovídající filtru.' 
            : 'Zatím nebyly přidány žádné práce.'}
        </div>
      ) : (
        <div className="works-grid">
          {finalWorks.map((work) => (
            <Link key={work.id} to={`/works/${work.id}`} className="work-card">
              <h3>{work.title}</h3>
              <p className="student-name">Autor: {work.studentName}</p>
              <p className="description">{work.description || 'Bez popisu'}</p>
              {work.tags && work.tags.length > 0 && (
                <div className="tags">
                  {work.tags.map(tag => (
                    <span 
                      key={tag.id} 
                      className="tag clickable" 
                      onClick={(e) => {
                        console.log('List tag clicked:', tag.name);
                        e.preventDefault();
                        e.stopPropagation();
                        toggleTag(tag.name);
                      }}
                      onMouseDown={(e) => e.stopPropagation()}
                    >
                      {tag.name}
                    </span>
                  ))}
                </div>
              )}
              <div className="work-meta">
                <span className="date">
                  {new Date(work.submittedAt).toLocaleDateString('cs-CZ')}
                </span>
                <span className="comments-count">
                  {work.comments?.length || 0} komentářů
                </span>
                {work.averageRating > 0 && (
                  <span className="rating">
                    ⭐ {work.averageRating.toFixed(1)} ({work.ratingCount})
                  </span>
                )}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default FinalWorksList;