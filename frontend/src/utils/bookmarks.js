// Bookmarks utility functions using localStorage

export const getBookmarks = () => {
  const bookmarks = localStorage.getItem('bookmarks');
  return bookmarks ? JSON.parse(bookmarks) : [];
};

export const addBookmark = (workId) => {
  const bookmarks = getBookmarks();
  if (!bookmarks.includes(workId)) {
    bookmarks.push(workId);
    localStorage.setItem('bookmarks', JSON.stringify(bookmarks));
  }
};

export const removeBookmark = (workId) => {
  const bookmarks = getBookmarks();
  const filtered = bookmarks.filter(id => id !== workId);
  localStorage.setItem('bookmarks', JSON.stringify(filtered));
};

export const isBookmarked = (workId) => {
  const bookmarks = getBookmarks();
  return bookmarks.includes(workId);
};

export const getBookmarkedWorks = (allWorks) => {
  const bookmarks = getBookmarks();
  return allWorks.filter(work => bookmarks.includes(work.id));
};

