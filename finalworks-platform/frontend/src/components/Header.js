import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

const Header = () => {
  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          <h1>Final Works Platform</h1>
        </Link>
        <nav>
          <Link to="/" className="nav-link">All Works</Link>
        </nav>
      </div>
    </header>
  );
};

export default Header;

