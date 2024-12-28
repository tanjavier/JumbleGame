package asia.fourtitude.interviewq.jumble.core;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class JumbleEngine {

    /**
     * From the input `word`, produces/generates a copy which has the same
     * letters, but in different ordering.
     *
     * Example: from "elephant" to "aeehlnpt".
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#scramble()
     * b) scrambled letters/output must not be the same as input
     *
     * @param word  The input word to scramble the letters.
     * @return  The scrambled output/letters.
     */
    public String scramble(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        
        // Convert string to char array for manipulation
        char[] letters = word.toCharArray();
        Random random = new Random();
        
        // Keep shuffling until we get a different arrangement
        String scrambled;
        do {
            // Fisher-Yates shuffle algorithm
            for (int i = letters.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                // Swap letters[i] with letters[j]
                char temp = letters[i];
                letters[i] = letters[j];
                letters[j] = temp;
            }
            scrambled = new String(letters);
        } while (scrambled.equals(word)); // Ensure the scrambled word is different
        
        return scrambled;
    }    

    /**
     * Retrieves the palindrome words from the internal
     * word list/dictionary ("src/main/resources/words.txt").
     *
     * Word of single letter is not considered as valid palindrome word.
     *
     * Examples: "eye", "deed", "level".
     *
     * Evaluation/Grading:
     * a) able to access/use resource from classpath
     * b) using inbuilt Collections
     * c) using "try-with-resources" functionality/statement
     * d) pass unit test: JumbleEngineTest#palindrome()
     *
     * @return  The list of palindrome words found in system/engine.
     * @see https://www.google.com/search?q=palindrome+meaning
     */
    public Collection<String> retrievePalindromeWords() {
        Collection<String> palindromes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("words.txt")))) {
                
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                // Skip single letter words
                if (word.length() <= 1) {
                    continue;
                }
                
                // Check if word is palindrome
                boolean isPalindrome = true;
                for (int i = 0; i < word.length() / 2; i++) {
                    if (word.charAt(i) != word.charAt(word.length() - 1 - i)) {
                        isPalindrome = false;
                        break;
                    }
                }
                
                if (isPalindrome) {
                    palindromes.add(word);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading words file", e);
        }
        
        return palindromes;
    }

    private List<String> wordList = null;

    private void loadWords() {
        if (wordList == null) {
            wordList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("words.txt")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    wordList.add(line.trim().toLowerCase());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading words file", e);
            }
        }
    }

    /**
     * Picks one word randomly from internal word list.
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#randomWord()
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param length  The word picked, must of length.
     * @return  One of the word (randomly) from word list.
     *          Or null if none matching.
     */
    public String pickOneRandomWord(Integer length) {
        loadWords();
        
        // If no length specified, return any random word
        if (length == null) {
            return wordList.get(new Random().nextInt(wordList.size()));
        }
        
        // Filter words by length
        List<String> matchingWords = wordList.stream()
            .filter(word -> word.length() == length)
            .collect(Collectors.toList());
        
        if (matchingWords.isEmpty()) {
            return null;
        }
        
        return matchingWords.get(new Random().nextInt(matchingWords.size()));
    }

    /**
     * Checks if the `word` exists in internal word list.
     * Matching is case insensitive.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word  The input word to check.
     * @return  true if `word` exists in internal word list.
     */
    public boolean exists(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        
        loadWords();
        return wordList.contains(word.toLowerCase());
    }

    /**
     * Finds all the words from internal word list which begins with the
     * input `prefix`.
     * Matching is case insensitive.
     *
     * Invalid `prefix` (null, empty string, blank string, non letter) will
     * return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param prefix  The prefix to match.
     * @return  The list of words matching the prefix.
     */
    public Collection<String> wordsMatchingPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty() || !prefix.matches("[a-zA-Z]+")) {
            return Collections.emptyList();
        }
        
        loadWords();
        String lowercasePrefix = prefix.toLowerCase();
        
        return wordList.stream()
            .filter(word -> word.startsWith(lowercasePrefix))
            .collect(Collectors.toList());
    }

    /**
     * Finds all the words from internal word list that is matching
     * the searching criteria.
     *
     * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
     * `length`, if have value, must be positive integer (>= 1).
     *
     * Words are filtered using `startChar` and `endChar` first.
     * Then apply `length` on the result, to produce the final output.
     *
     * Must have at least one valid value out of 3 inputs
     * (`startChar`, `endChar`, `length`) to proceed with searching.
     * Otherwise, return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param startChar  The first character of the word to search for.
     * @param endChar    The last character of the word to match with.
     * @param length     The length of the word to match.
     * @return  The list of words matching the searching criteria.
     */
    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        // Validate at least one criteria is provided
        if (startChar == null && endChar == null && length == null) {
            return Collections.emptyList();
        }
        
        // Validate character inputs are letters
        if ((startChar != null && !Character.isLetter(startChar)) || 
            (endChar != null && !Character.isLetter(endChar))) {
            return Collections.emptyList();
        }
        
        // Validate length is positive
        if (length != null && length <= 0) {
            return Collections.emptyList();
        }
        
        loadWords();
        
        String startCharLower = startChar != null ? Character.toLowerCase(startChar) + "" : null;
        String endCharLower = endChar != null ? Character.toLowerCase(endChar) + "" : null;
        
        return wordList.stream()
            .filter(word -> 
                (startCharLower == null || word.startsWith(startCharLower)) &&
                (endCharLower == null || word.endsWith(endCharLower)) &&
                (length == null || word.length() == length))
            .collect(Collectors.toList());
    }

    /**
     * Generates all possible combinations of smaller/sub words using the
     * letters from input word.
     *
     * The `minLength` set the minimum length of sub word that is considered
     * as acceptable word.
     *
     * If length of input `word` is less than `minLength`, then return empty list.
     *
     * Example: From "yellow" and `minLength` = 3, the output sub words:
     *     low, lowly, lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word       The input word to use as base/seed.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The list of sub words constructed from input `word`.
     */
    public Collection<String> generateSubWords(String word, Integer minLength) {
        // Handle invalid inputs
        if (word == null || word.trim().isEmpty() || !word.matches("[a-zA-Z]+")) {
            return Collections.emptyList();
        }
        
        // Set default minLength to 3 if null
        if (minLength == null) {
            minLength = 3;
        }
        
        // Return empty list if minLength is 0 or greater than word length
        if (minLength <= 0 || minLength > word.length()) {
            return Collections.emptyList();
        }
        
        // Convert word to lowercase
        word = word.toLowerCase();
        
        loadWords();
        Set<String> subWords = new TreeSet<>();
        
        // Count frequency of each letter in the input word
        int[] inputFreq = new int[26];
        for (char c : word.toCharArray()) {
            inputFreq[c - 'a']++;
        }
        
        // Check each dictionary word
        for (String dictWord : wordList) {
            if (dictWord.length() >= minLength && dictWord.length() <= word.length()) {
                // Count frequency of each letter in the dictionary word
                int[] dictFreq = new int[26];
                for (char c : dictWord.toCharArray()) {
                    dictFreq[c - 'a']++;
                }
                
                // Check if dictionary word can be formed from input word letters
                boolean canForm = true;
                for (int i = 0; i < 26; i++) {
                    if (dictFreq[i] > inputFreq[i]) {
                        canForm = false;
                        break;
                    }
                }
                
                // Add word if it can be formed and is not the same as input word
                if (canForm && !dictWord.equals(word)) {
                    subWords.add(dictWord);
                }
            }
        }
        
        return subWords;
    }

    /**
     * Creates a game state with word to guess, scrambled letters, and
     * possible combinations of words.
     *
     * Word is of length 6 characters.
     * The minimum length of sub words is of length 3 characters.
     *
     * @param length     The length of selected word.
     *                   Expects >= 3.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The game state.
     */
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

}
