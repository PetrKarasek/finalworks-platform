import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import './App.css';
import HomePage from './components/HomePage';
import FinalWorksList from './components/FinalWorksList';
import FinalWorkDetail from './components/FinalWorkDetail';
import UploadWork from './components/UploadWork';
import BookmarksPage from './components/BookmarksPage';
import AdminPanel from './components/AdminPanel';
import LoginPage from './components/LoginPage';
import Header from './components/Header';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/works" element={<FinalWorksList />} />
              <Route path="/works/:id" element={<FinalWorkDetail />} />
              <Route path="/upload" element={<UploadWork />} />
              <Route path="/bookmarks" element={<BookmarksPage />} />
              <Route path="/admin" element={<AdminPanel />} />
              <Route path="/login" element={<LoginPage />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;

