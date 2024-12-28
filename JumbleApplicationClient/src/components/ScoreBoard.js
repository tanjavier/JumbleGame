import React from 'react';

const ScoreBoard = ({ score, remainingGuesses, timeLeft }) => {
  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'space-between', 
      marginBottom: '20px',
      padding: '10px',
      backgroundColor: '#f8f9fa',
      border: '1px solid #dee2e6',
      borderRadius: '4px'
    }}>
      <div>
        <strong>Score: </strong>
        <span>{score}</span>
      </div>
      <div>
        <strong>Guesses Left: </strong>
        <span>{remainingGuesses}</span>
      </div>
      <div>
        <strong>Time Left: </strong>
        <span>{timeLeft}s</span>
      </div>
    </div>
  );
};

export default ScoreBoard;