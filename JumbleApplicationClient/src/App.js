import React from 'react';
import GameBoard from './components/GameBoard';

const App = () => {
  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
      <h1 style={{ textAlign: 'center', marginBottom: '20px' }}>Jumble Word Game</h1>
      <GameBoard />
    </div>
  );
};

export default App;