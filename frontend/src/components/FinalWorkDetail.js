import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { finalWorksAPI, ratingsAPI, bookmarksAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './FinalWorkDetail.css';

const FinalWorkDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated, isAdmin } = useAuth();
  const [finalWork, setFinalWork] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newComment, setNewComment] = useState({ content: '' });
  const [userRating, setUserRating] = useState(0);
  const [bookmarked, setBookmarked] = useState(false);

  const fetchFinalWork = useCallback(async () => {
    try {
      const response = await finalWorksAPI.getById(id);
      setFinalWork(response.data);
      setLoading(false);
    } catch (err) {
      setError('Nepodařilo se načíst práci');
      setLoading(false);
      console.error(err);
    }
  }, [id]);

  const fetchComments = useCallback(async () => {
    try {
      const response = await finalWorksAPI.getComments(id);
      setComments(response.data);
    } catch (err) {
      console.error('Failed to load comments', err);
    }
  }, [id]);

  useEffect(() => {
    fetchFinalWork();
    fetchComments();
  }, [fetchComments, fetchFinalWork]);

  const handleSubmitComment = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      alert('Pro komentování se musíte přihlásit');
      return;
    }
    if (!newComment.content.trim()) {
      alert('Prosím napište komentář');
      return;
    }

    try {
      await finalWorksAPI.addComment(id, { content: newComment.content });
      setNewComment({ content: '' });
      fetchComments();
      fetchFinalWork();
    } catch (err) {
      alert('Nepodařilo se odeslat komentář');
      console.error(err);
    }
  };

  const handleRating = async (rating) => {
    if (!isAuthenticated) return;
    try {
      await ratingsAPI.rate(Number(id), rating);
      setUserRating(rating);
      // Refresh work data to get updated average rating
      fetchFinalWork();
    } catch (err) {
      alert('Nepodařilo se ohodnotit práci');
      console.error(err);
    }
  };

  const handleBookmark = async () => {
    if (!isAuthenticated) return;
    try {
      if (bookmarked) {
        await bookmarksAPI.removeBookmark(Number(id));
        setBookmarked(false);
      } else {
        await bookmarksAPI.bookmark(Number(id));
        setBookmarked(true);
      }
    } catch (err) {
      alert('Nepodařilo se upravit záložku');
      console.error(err);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Opravdu chcete smazat tuto práci?')) {
      return;
    }

    try {
      await finalWorksAPI.delete(id);
      window.location.href = '/';
    } catch (err) {
      alert('Nepodařilo se smazat práci');
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Načítání...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!finalWork) return <div className="error">Práce nenalezena</div>;

  return (
    <div className="final-work-detail">
      <Link to="/" className="back-link">← Zpět na všechny práce</Link>
      
      <div className="work-header">
        <div className="work-title-row">
          <h1>{finalWork.title}</h1>
          <div className="work-actions">
            {isAuthenticated && (
              <button
                onClick={handleBookmark}
                className={`bookmark-btn ${bookmarked ? 'bookmarked' : ''}`}
                title={bookmarked ? 'Odebrat ze záložek' : 'Přidat do záložek'}
              >
                {bookmarked ? '🔖' : '🔗'}
              </button>
            )}
            {isAdmin && (
              <button onClick={handleDelete} className="delete-btn" title="Smazat práci">
                🗑️
              </button>
            )}
          </div>
        </div>
        <div className="work-info">
          <span className="student-info">Autor: {finalWork.studentName} ({finalWork.studentEmail})</span>
          <span className="date">
            Přidáno: {new Date(finalWork.submittedAt).toLocaleString('cs-CZ')}
          </span>
        </div>
        {finalWork.tags && finalWork.tags.length > 0 && (
          <div className="tags-section">
            <span className="tags-label">Tagy:</span>
            <div className="tags">
              {finalWork.tags.map(tag => (
                <span 
                  key={tag.id} 
                  className="tag clickable" 
                  onClick={() => {
                    console.log('Detail tag clicked:', tag.name);
                    console.log('Navigating to:', '/works?tags=' + encodeURIComponent(tag.name));
                    navigate('/works?tags=' + encodeURIComponent(tag.name));
                  }}
                >
                  {tag.name}
                </span>
              ))}
            </div>
          </div>
        )}
        <div className="rating-section">
          <span className="rating-label">Hodnocení:</span>
          <div className="star-rating">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                className={`star ${star <= userRating ? 'active' : ''}`}
                onClick={() => handleRating(star)}
                disabled={!isAuthenticated}
                title={`Ohodnotit ${star} ${star === 1 ? 'hvězdičkou' : 'hvězdičkami'}`}
              >
                ⭐
              </button>
            ))}
            {userRating > 0 && (
              <span className="rating-value">({userRating}/5)</span>
            )}
          </div>
        </div>
      </div>

      {finalWork.description && (
        <div className="work-description">
          <h2>Popis</h2>
          <p>{finalWork.description}</p>
        </div>
      )}

      {finalWork.fileUrl && (
        <div className="work-file">
          <h2>Soubor</h2>
          <a href={finalWork.fileUrl} target="_blank" rel="noopener noreferrer" className="file-link">
            Zobrazit/Stáhnout soubor
          </a>
        </div>
      )}

      <div className="comments-section">
        <h2>Komentáře ({comments.length})</h2>

        {!isAuthenticated ? (
          <div className="no-comments">
            Pro přidání komentáře se musíte <Link to="/login">přihlásit</Link>.
          </div>
        ) : (
          <form onSubmit={handleSubmitComment} className="comment-form">
            <div className="form-group">
              <label htmlFor="content">Komentář</label>
              <textarea
                id="content"
                value={newComment.content}
                onChange={(e) => setNewComment({ ...newComment, content: e.target.value })}
                placeholder="Napište váš komentář..."
                rows="4"
                required
              />
            </div>
            {user && <div className="form-group"><small>Přihlášen jako: {user.name}</small></div>}
            <button type="submit" className="submit-button">Odeslat komentář</button>
          </form>
        )}

        <div className="comments-list">
          {comments.length === 0 ? (
            <div className="no-comments">Zatím žádné komentáře. Buďte první, kdo komentuje!</div>
          ) : (
            comments.map((comment) => (
              <div key={comment.id} className="comment-card">
                <div className="comment-header">
                  <span className="comment-author">{comment.authorName}</span>
                  <span className="comment-date">
                    {new Date(comment.createdAt).toLocaleString('cs-CZ')}
                  </span>
                  {isAdmin && (
                    <button
                      onClick={async () => {
                        if (window.confirm('Opravdu chcete smazat tento komentář?')) {
                          try {
                            await finalWorksAPI.deleteComment(comment.id);
                            fetchComments();
                          } catch (err) {
                            alert('Nepodařilo se smazat komentář');
                          }
                        }
                      }}
                      className="delete-comment-btn"
                      title="Smazat komentář"
                    >
                      🗑️
                    </button>
                  )}
                </div>
                <p className="comment-content">{comment.content}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default FinalWorkDetail;

