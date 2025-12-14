import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Header.css';

const Header = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          <h1>Akademický portál</h1>
        </Link>
        <nav className="nav-menu">
          <Link to="/" className="nav-link">Domů</Link>
          <Link to="/works" className="nav-link">Všechny práce</Link>
          {isAuthenticated && <Link to="/upload" className="nav-link">Nahrát práci</Link>}
          {isAuthenticated && <Link to="/bookmarks" className="nav-link">Záložky</Link>}
          {isAdmin && (
            <Link to="/admin" className="nav-link admin-link">Admin</Link>
          )}
          {user ? (
            <>
              <span className="user-info">Přihlášen: {user.name}</span>
              <button onClick={logout} className="logout-btn">
                Odhlásit
              </button>
            </>
          ) : (
            <Link to="/login" className="nav-link login-link">Přihlásit</Link>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;

