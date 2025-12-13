// Ratings utility functions using localStorage
// Note: In a real app, this would be stored in the backend

export const getRatings = () => {
  const ratings = localStorage.getItem('ratings');
  return ratings ? JSON.parse(ratings) : {};
};

export const rateWork = (workId, rating) => {
  const ratings = getRatings();
  ratings[workId] = rating;
  localStorage.setItem('ratings', JSON.stringify(ratings));
};

export const getWorkRating = (workId) => {
  const ratings = getRatings();
  return ratings[workId] || 0;
};

export const getAverageRating = (workId) => {
  // In a real app, this would come from the backend
  // For now, we'll use localStorage
  return getWorkRating(workId);
};

export const getAllRatings = () => {
  return getRatings();
};

