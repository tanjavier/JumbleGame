import React from 'react';

const GameSettings = ({ difficulty, onDifficultyChange, disabled }) => {
  return (
    <div style={{ marginBottom: '20px' }}>
      <h3>Choose you difficulty</h3>
      <div>
        <label>
          Difficulty:
          <select 
            value={difficulty || ''}
            onChange={(e) => onDifficultyChange(e.target.value)}
            style={{ marginLeft: '10px' }}
            disabled={disabled}
          >
            <option value="" disabled>Select difficulty</option>
            <option value="easy">Easy (8 guesses, 30s per word)</option>
            <option value="medium">Medium (6 guesses, 20s per word)</option>
            <option value="hard">Hard (5 guesses, 15s per word)</option>
          </select>
        </label>
      </div>
    </div>
  );
};

export const difficultySettings = {
  easy: { maxGuesses: 8, timeLimit: 30, pointsPerWord: 10, penalty: 2 },
  medium: { maxGuesses: 6, timeLimit: 20, pointsPerWord: 15, penalty: 3 },
  hard: { maxGuesses: 5, timeLimit: 15, pointsPerWord: 20, penalty: 5 }
};

export default GameSettings;