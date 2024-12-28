import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import GuessForm from './GuessForm';

describe('GuessForm', () => {
    test('should render input and submit button', () => {
        render(<GuessForm onSubmit={() => {}} />);
        
        expect(screen.getByPlaceholderText('Enter your guess')).toBeInTheDocument();
        expect(screen.getByText('Submit')).toBeInTheDocument();
    });

    test('should call onSubmit with trimmed lowercase word', () => {
        const mockSubmit = jest.fn();
        render(<GuessForm onSubmit={mockSubmit} />);

        const input = screen.getByPlaceholderText('Enter your guess');
        fireEvent.change(input, { target: { value: '  TEST  ' } });
        fireEvent.submit(input.closest('form'));

        expect(mockSubmit).toHaveBeenCalledWith('test');
    });

    test('should not submit if word is too short', () => {
        const mockSubmit = jest.fn();
        render(<GuessForm onSubmit={mockSubmit} />);

        const input = screen.getByPlaceholderText('Enter your guess');
        fireEvent.change(input, { target: { value: 'ab' } });
        fireEvent.submit(input.closest('form'));

        expect(mockSubmit).not.toHaveBeenCalled();
    });

    test('should clear input after submission', () => {
        const mockSubmit = jest.fn();
        render(<GuessForm onSubmit={mockSubmit} />);

        const input = screen.getByPlaceholderText('Enter your guess');
        fireEvent.change(input, { target: { value: 'test' } });
        fireEvent.submit(input.closest('form'));

        expect(input.value).toBe('');
    });

    test('should handle empty submission', () => {
        const mockSubmit = jest.fn();
        render(<GuessForm onSubmit={mockSubmit} />);

        const input = screen.getByPlaceholderText('Enter your guess');
        fireEvent.submit(input.closest('form'));

        expect(mockSubmit).not.toHaveBeenCalled();
    });
});