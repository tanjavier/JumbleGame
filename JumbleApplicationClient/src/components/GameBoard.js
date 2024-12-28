import React, { useState, useEffect } from 'react';
import axios from 'axios';
import GuessForm from './GuessForm';
import GameSettings, { difficultySettings } from './GameSettings';
import ScoreBoard from './ScoreBoard';

const API_URL = 'http://localhost:8080/api/game';

const GameBoard = () => {
  const [gameState, setGameState] = useState(null);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [score, setScore] = useState(0);
  const [difficulty, setDifficulty] = useState(null);
  const [remainingGuesses, setRemainingGuesses] = useState(null);
  const [timeLeft, setTimeLeft] = useState(null);
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [gameStarted, setGameStarted] = useState(false);

  const calculateScore = (timeTaken) => {
    const settings = difficultySettings[difficulty];
    const timeBonus = Math.floor((settings.timeLimit - timeTaken) / 2);
    return Math.max(settings.pointsPerWord + timeBonus, 1);
  };

  const startNewGame = async () => {
    if (!difficulty) {
      setMessage("Please select a difficulty level first");
      return;
    }

    try {
      const response = await axios.get(`${API_URL}/new`);
      setGameState(response.data);
      setError(null);
      setMessage('New game started!');
      setScore(0);
      const currentSettings = difficultySettings[difficulty];
      setRemainingGuesses(currentSettings.maxGuesses);
      setTimeLeft(currentSettings.timeLimit);
      setIsTimerRunning(true);
      setGameStarted(true);
    } catch (err) {
      setError('Failed to start new game. Please try again.');
    }
  };

  const submitGuess = async (guess) => {
    if (remainingGuesses <= 0) {
      setMessage("No more guesses left!");
      return;
    }

    try {
      // Check for duplicate guess before making API call
      const isDuplicate = gameState.guessed_words.includes(guess.toLowerCase());
      if (isDuplicate) {
        const penalty = difficultySettings[difficulty].penalty;
        setScore(prev => Math.max(0, prev - penalty));
        setMessage(`Word already guessed! -${penalty} points`);
        setRemainingGuesses(prev => prev - 1);
        return;
      }

      const response = await axios.post(`${API_URL}/guess`, {
        id: gameState.id,
        word: guess
      });
      
      const newGameState = response.data;
      const oldGuessedWords = gameState.guessed_words.length;
      const newGuessedWords = newGameState.guessed_words.length;
      const isCorrect = newGuessedWords > oldGuessedWords;
      
      setGameState(newGameState);
      
      if (isCorrect) {
        const points = calculateScore(difficultySettings[difficulty].timeLimit - timeLeft);
        setScore(prev => prev + points);
        setMessage(`Correct guess! +${points} points!`);
      } else {
        const penalty = difficultySettings[difficulty].penalty;
        setScore(prev => Math.max(0, prev - penalty));
        setMessage(`Incorrect guess! -${penalty} points`);
      }

      setRemainingGuesses(prev => prev - 1);

      if (newGameState.remaining_words === 0 || remainingGuesses <= 1) {
        setIsTimerRunning(false);
      }

    } catch (err) {
      setError('Failed to submit guess. Please try again.');
    }
  };

  const handleDifficultyChange = (newDifficulty) => {
    setDifficulty(newDifficulty);
    setRemainingGuesses(difficultySettings[newDifficulty].maxGuesses);
    setTimeLeft(difficultySettings[newDifficulty].timeLimit);
    setMessage("Click Start Game to begin!");
  };

  useEffect(() => {
    setMessage("Please select a difficulty level");
  }, []);

  useEffect(() => {
    let timer;
    if (gameStarted && isTimerRunning && timeLeft > 0) {
      timer = setInterval(() => {
        setTimeLeft(prev => {
          if (prev <= 1) {
            setIsTimerRunning(false);
            setMessage("Time's up!");
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isTimerRunning, timeLeft, difficulty, gameStarted]);

  if (!gameStarted) {
    return (
      <div style={{ padding: '20px' }}>
        <GameSettings 
          difficulty={difficulty} 
          onDifficultyChange={handleDifficultyChange}
          disabled={false}
        />
        <div style={{ textAlign: 'center', marginTop: '20px' }}>
          <div style={{ marginBottom: '20px' }}>{message}</div>
          {difficulty && (
            <button
              onClick={startNewGame}
              style={{
                padding: '12px 24px',
                backgroundColor: '#4CAF50',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '16px'
              }}
            >
              Start Game
            </button>
          )}
        </div>
      </div>
    );
  }

  return (
    <div style={{ padding: '20px' }}>
      <GameSettings 
        difficulty={difficulty} 
        onDifficultyChange={handleDifficultyChange}
        disabled={true}
      />
      
      <ScoreBoard 
        score={score}
        remainingGuesses={remainingGuesses}
        timeLeft={timeLeft}
      />

      <div style={{ marginBottom: '20px' }}>
        <p>Scrambled Word: <strong>{gameState.scramble_word}</strong></p>
        <p>Total Words: {gameState.total_words}</p>
        <p>Remaining Words: {gameState.remaining_words}</p>
      </div>

      {message && (
        <div style={{ 
          padding: '10px', 
          marginBottom: '10px',
          backgroundColor: message.includes('Correct') ? '#e8f5e9' : '#ffebee',
          border: '1px solid',
          borderColor: message.includes('Correct') ? '#a5d6a7' : '#ef9a9a'
        }}>
          {message}
        </div>
      )}

      {error && (
        <div style={{ 
          padding: '10px', 
          marginBottom: '10px', 
          backgroundColor: '#ffebee',
          border: '1px solid #ef9a9a'
        }}>
          {error}
        </div>
      )}

      {remainingGuesses > 0 && timeLeft > 0 && (
        <GuessForm onSubmit={submitGuess} />
      )}

      <div style={{ marginTop: '20px' }}>
        <h3>Guessed Words:</h3>
        {gameState.guessed_words.length === 0 ? (
          <p>No words guessed yet.</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {gameState.guessed_words.map((word, index) => (
              <li key={index} style={{ marginBottom: '5px' }}>{word}</li>
            ))}
          </ul>
        )}
      </div>

      {(gameState.remaining_words === 0 || remainingGuesses <= 0 || timeLeft <= 0) && (
        <div style={{ 
          marginTop: '20px',
          padding: '10px',
          backgroundColor: '#e8f5e9',
          border: '1px solid #a5d6a7',
          textAlign: 'center'
        }}>
          <p>Game Over! Final Score: {score}</p>
          <button
            onClick={() => {
              setGameStarted(false);
              setGameState(null);
              setMessage("Please select a difficulty level");
            }}
            style={{
              padding: '8px 16px',
              backgroundColor: '#4caf50',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              marginTop: '10px'
            }}
          >
            Start New Game
          </button>
        </div>
      )}
    </div>
  );
};

export default GameBoard;