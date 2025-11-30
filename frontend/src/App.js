import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import FinalWorksList from './components/FinalWorksList';
import FinalWorkDetail from './components/FinalWorkDetail';
import Header from './components/Header';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<FinalWorksList />} />
            <Route path="/works/:id" element={<FinalWorkDetail />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;

