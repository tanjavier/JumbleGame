package asia.fourtitude.interviewq.jumble.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import asia.fourtitude.interviewq.jumble.TestConfig;
import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.GameGuessInput;
import asia.fourtitude.interviewq.jumble.model.GameGuessOutput;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
class GameApiControllerTest {

    static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    JumbleEngine jumbleEngine;

    /*
     * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
     * as reference. Search internet for resource/tutorial/help in implementing
     * the unit tests.
     *
     * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
     * documentation and perform testing.
     *
     * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
     * for REST API documentation and perform testing.
     */

    @Test
    void whenCreateNewGame_thenSuccess() throws Exception {
        /*
         * Doing HTTP GET "/api/game/new"
         *
         * Input: None
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Created new game."
         * c) `id` is not null
         * d) `originalWord` is not null
         * e) `scrambleWord` is not null
         * f) `totalWords` > 0
         * g) `remainingWords` > 0 and same as `totalWords`
         * h) `guessedWords` is empty list
         */
        MvcResult result = mvc.perform(get("/api/game/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
                
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        
        assertEquals("Created new game.", output.getResult());
        assertNotNull(output.getId());
        assertNotNull(output.getOriginalWord());
        assertNotNull(output.getScrambleWord());
        assertTrue(output.getTotalWords() > 0);
        assertEquals(output.getTotalWords(), output.getRemainingWords());
        assertTrue(output.getGuessedWords().isEmpty());
    }

    @Test
    void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is null or missing
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Invalid Game ID."
         */
        GameGuessInput input = new GameGuessInput();
        input.setWord("test");

        MvcResult result = mvc.perform(post("/api/game/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OM.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andReturn();
                
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("Invalid Game ID.", output.getResult());    
    }

    @Test
    void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is some valid ID (but not exists in game system)
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Game board/state not found."
         */
        GameGuessInput input = new GameGuessInput();
        input.setId(UUID.randomUUID().toString());
        input.setWord("test");

        MvcResult result = mvc.perform(post("/api/game/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OM.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andReturn();
                
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("Game board/state not found.", output.getResult());
    }

    @Test
    void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is null or missing
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` is equals to `input.word`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */

        // First create a game
        MvcResult result = mvc.perform(get("/api/game/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        GameGuessOutput newGame = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        
        // Submit null word
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        // Setting word to null
        input.setWord(null);

        result = mvc.perform(post("/api/game/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();
                
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("Guessed incorrectly.", output.getResult());
        assertEquals(newGame.getId(), output.getId());
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        assertNotNull(output.getScrambleWord());
        assertNull(output.getGuessWord());  // Changed this line - expect null
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        assertEquals(newGame.getRemainingWords(), output.getRemainingWords());
        assertTrue(output.getGuessedWords().isEmpty());
    }

    @Test
    void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is some value (that is not correct answer)
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
          
        // First create a game
        MvcResult result = mvc.perform(get("/api/game/new")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        GameGuessOutput newGame = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // Submit an incorrect word
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord("wrongword"); // This word won't be in the subwords list

        result = mvc.perform(post("/api/game/guess")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OM.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andReturn();
            
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("Guessed incorrectly.", output.getResult());
        assertEquals(newGame.getId(), output.getId());
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        assertNotNull(output.getScrambleWord());
        assertEquals("wrongword", output.getGuessWord());
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        assertEquals(newGame.getRemainingWords(), output.getRemainingWords());  // No change in remaining words
        assertTrue(output.getGuessedWords().isEmpty());  // No words guessed yet
    }

    @Test
    void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed correctly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords - 1` of previous game state (decrement by 1)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */

        // First create a game
        MvcResult result = mvc.perform(get("/api/game/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        GameGuessOutput newGame = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        
        // Get a valid word from the game state
        String validWord = new ArrayList<>(jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3)).get(0);
        
        // Submit valid word
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord(validWord);

        result = mvc.perform(post("/api/game/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();
                
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("Guessed correctly.", output.getResult());
        assertEquals(newGame.getId(), output.getId());
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        assertNotNull(output.getScrambleWord());
        assertEquals(validWord, output.getGuessWord());
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        assertEquals(newGame.getRemainingWords() - 1, output.getRemainingWords());
        assertEquals(1, output.getGuessedWords().size());
        assertTrue(output.getGuessedWords().contains(validWord));
    }

    @Test
    void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         * b) has submit all correct answers, except the last answer
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of the last correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "All words guessed."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is 0 (no more remaining, game ended)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */

        // First create a game
        MvcResult result = mvc.perform(get("/api/game/new")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        GameGuessOutput newGame = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);

        // Get all valid words
        List<String> validWords = new ArrayList<>(jumbleEngine.generateSubWords(newGame.getOriginalWord(), 3));

        // Submit all words except last one
        for (int i = 0; i < validWords.size() - 1; i++) {
        String word = validWords.get(i);
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord(word);

        mvc.perform(post("/api/game/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OM.writeValueAsString(input)))
                .andExpect(status().isOk());
        }

        // Submit last word
        String lastWord = validWords.get(validWords.size() - 1);
        GameGuessInput input = new GameGuessInput();
        input.setId(newGame.getId());
        input.setWord(lastWord);

        result = mvc.perform(post("/api/game/guess")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OM.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andReturn();
            
        GameGuessOutput output = OM.readValue(result.getResponse().getContentAsString(), GameGuessOutput.class);
        assertEquals("All words guessed.", output.getResult());
        assertEquals(newGame.getId(), output.getId());
        assertEquals(newGame.getOriginalWord(), output.getOriginalWord());
        assertNotNull(output.getScrambleWord());
        assertEquals(lastWord, output.getGuessWord());
        assertEquals(newGame.getTotalWords(), output.getTotalWords());
        assertEquals(0, output.getRemainingWords());
        assertEquals(validWords.size(), output.getGuessedWords().size());
        assertTrue(output.getGuessedWords().contains(lastWord));
    }

}
