import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import './FinalWorkDetail.css';

const FinalWorkDetail = () => {
  const { id } = useParams();
  const [finalWork, setFinalWork] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newComment, setNewComment] = useState({ content: '', authorName: '' });

  useEffect(() => {
    fetchFinalWork();
    fetchComments();
  }, [id]);

  const fetchFinalWork = async () => {
    try {
      const response = await axios.get(`https://localhost:8443/api/final-works/${id}`);
      setFinalWork(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load final work');
      setLoading(false);
      console.error(err);
    }
  };

  const fetchComments = async () => {
    try {
      const response = await axios.get(`https://localhost:8443/api/final-works/${id}/comments`);
      setComments(response.data);
    } catch (err) {
      console.error('Failed to load comments', err);
    }
  };

  const handleSubmitComment = async (e) => {
    e.preventDefault();
    if (!newComment.content.trim() || !newComment.authorName.trim()) {
      alert('Please fill in all fields');
      return;
    }

    try {
      await axios.post(`https://localhost:8443/api/final-works/${id}/comments`, newComment);
      setNewComment({ content: '', authorName: '' });
      fetchComments();
      fetchFinalWork();
    } catch (err) {
      alert('Failed to submit comment');
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!finalWork) return <div className="error">Final work not found</div>;

  return (
    <div className="final-work-detail">
      <Link to="/" className="back-link">← Back to all works</Link>
      
      <div className="work-header">
        <h1>{finalWork.title}</h1>
        <div className="work-info">
          <span className="student-info">By: {finalWork.studentName} ({finalWork.studentEmail})</span>
          <span className="date">
            Submitted: {new Date(finalWork.submittedAt).toLocaleString()}
          </span>
        </div>
      </div>

      {finalWork.description && (
        <div className="work-description">
          <h2>Description</h2>
          <p>{finalWork.description}</p>
        </div>
      )}

      {finalWork.fileUrl && (
        <div className="work-file">
          <h2>File</h2>
          <a href={finalWork.fileUrl} target="_blank" rel="noopener noreferrer" className="file-link">
            View/Download File
          </a>
        </div>
      )}

      <div className="comments-section">
        <h2>Comments ({comments.length})</h2>
        
        <form onSubmit={handleSubmitComment} className="comment-form">
          <div className="form-group">
            <label htmlFor="authorName">Your Name</label>
            <input
              type="text"
              id="authorName"
              value={newComment.authorName}
              onChange={(e) => setNewComment({ ...newComment, authorName: e.target.value })}
              placeholder="Enter your name"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="content">Comment</label>
            <textarea
              id="content"
              value={newComment.content}
              onChange={(e) => setNewComment({ ...newComment, content: e.target.value })}
              placeholder="Write your comment here..."
              rows="4"
              required
            />
          </div>
          <button type="submit" className="submit-button">Submit Comment</button>
        </form>

        <div className="comments-list">
          {comments.length === 0 ? (
            <div className="no-comments">No comments yet. Be the first to comment!</div>
          ) : (
            comments.map((comment) => (
              <div key={comment.id} className="comment-card">
                <div className="comment-header">
                  <span className="comment-author">{comment.authorName}</span>
                  <span className="comment-date">
                    {new Date(comment.createdAt).toLocaleString()}
                  </span>
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

