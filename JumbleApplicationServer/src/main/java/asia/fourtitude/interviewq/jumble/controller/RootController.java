package asia.fourtitude.interviewq.jumble.controller;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.ExistsForm;
import asia.fourtitude.interviewq.jumble.model.PrefixForm;
import asia.fourtitude.interviewq.jumble.model.ScrambleForm;
import asia.fourtitude.interviewq.jumble.model.SearchForm;
import asia.fourtitude.interviewq.jumble.model.SubWordsForm;

@Controller
@RequestMapping(path = "/")
public class RootController {

    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    private final JumbleEngine jumbleEngine;

    @Autowired(required = true)
    public RootController(JumbleEngine jumbleEngine) {
        this.jumbleEngine = jumbleEngine;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("timeNow", ZonedDateTime.now());
        return "index";
    }

    @GetMapping("scramble")
    public String doGetScramble(Model model) {
        model.addAttribute("form", new ScrambleForm());
        return "scramble";
    }

    @PostMapping("scramble")
    public String doPostScramble(
            @ModelAttribute(name = "form") ScrambleForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#scramble()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        if (form.getWord() == null || form.getWord().trim().isEmpty()) {
            bindingResult.rejectValue("word", "NotBlank", "must not be blank");
            return "scramble";
        }

        if (form.getWord().length() < 3 || form.getWord().length() > 30) {
            bindingResult.rejectValue("word", "Size", "size must be between 3 and 30");
            return "scramble";
        }

        String originalWord = form.getWord().trim();
        String scrambledWord = this.jumbleEngine.scramble(originalWord);
        
        // Keep trying until we get a different scramble
        while (scrambledWord.equals(originalWord)) {
            scrambledWord = this.jumbleEngine.scramble(originalWord);
        }
        
        model.addAttribute("scrambledWord", scrambledWord);
        return "scramble";
    }

    @GetMapping("palindrome")
    public String doGetPalindrome(Model model) {
        model.addAttribute("words", this.jumbleEngine.retrievePalindromeWords());
        return "palindrome";
    }

    @GetMapping("exists")
    public String doGetExists(Model model) {
        model.addAttribute("form", new ExistsForm());
        return "exists";
    }

    @PostMapping("exists")
    public String doPostExists(
            @ModelAttribute(name = "form") ExistsForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#exists()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        // Store original word (with spaces) for display
        String originalWord = form.getWord();
        // Use trimmed version for existence check
        String trimmedWord = originalWord.trim();
        
        if (trimmedWord.isEmpty()) {
            bindingResult.rejectValue("word", "NotBlank", "must not be blank");
            return "exists";
        }
        
        // Check existence with trimmed word but display original word
        form.setExists(this.jumbleEngine.exists(trimmedWord));
        form.setWord(originalWord);  // Keep the original word with spaces for display
 
        return "exists";
    }

    @GetMapping("prefix")
    public String doGetPrefix(Model model) {
        model.addAttribute("form", new PrefixForm());
        return "prefix";
    }

    @PostMapping("prefix")
    public String doPostPrefix(
            @ModelAttribute(name = "form") PrefixForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#wordsMatchingPrefix()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        if (form.getPrefix() == null || form.getPrefix().trim().isEmpty()) {
            bindingResult.rejectValue("prefix", "NotBlank", "must not be blank");
            return "prefix";
        }

        // Store original prefix with spaces for display
        String originalPrefix = form.getPrefix();
        // Use trimmed version for searching
        String trimmedPrefix = originalPrefix.trim();
        
        // Get matching words and set them on the form
        Collection<String> matchingWords = this.jumbleEngine.wordsMatchingPrefix(trimmedPrefix);
        form.setPrefix(originalPrefix);  // Preserve original prefix with spaces
        form.setWords(matchingWords);
        
        return "prefix";
    }

    @GetMapping("search")
    public String doGetSearch(Model model) {
        model.addAttribute("form", new SearchForm());
        return "search";
    }

    @PostMapping("search")
    public String doPostSearch(
            @ModelAttribute(name = "form") SearchForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) Show the fields error accordingly: "Invalid startChar", "Invalid endChar", "Invalid length".
         * c) To call JumbleEngine#searchWords()
         * d) Presentation page to show the result
         * e) Must pass the corresponding unit tests
         */

        String startChar = form.getStartChar();
        String endChar = form.getEndChar();
        Integer length = form.getLength();

        // Check if there are type conversion errors
        if (bindingResult.hasFieldErrors("length")) {
            return "search";
        }

        // Check if all fields are empty/null
        if ((startChar == null || startChar.trim().isEmpty()) &&
            (endChar == null || endChar.trim().isEmpty()) &&
            length == null) {
            bindingResult.rejectValue("startChar", "Invalid", "Invalid startChar");
            bindingResult.rejectValue("endChar", "Invalid", "Invalid endChar");
            bindingResult.rejectValue("length", "Invalid", "Invalid length");
            return "search";
        }

        // Process start character
        Character startCharacter = null;
        if (startChar != null && !startChar.trim().isEmpty()) {
            if (startChar.length() > 1) {
                bindingResult.rejectValue("startChar", "Size", "size must be between 0 and 1");
                return "search";
            }
            startCharacter = startChar.trim().charAt(0);
            if (!Character.isLetter(startCharacter)) {
                bindingResult.rejectValue("startChar", "Invalid", "must be a letter");
                return "search";
            }
        }

        // Process end character
        Character endCharacter = null;
        if (endChar != null && !endChar.trim().isEmpty()) {
            if (endChar.length() > 1) {
                bindingResult.rejectValue("endChar", "Size", "size must be between 0 and 1");
                return "search";
            }
            endCharacter = endChar.trim().charAt(0);
            if (!Character.isLetter(endCharacter)) {
                bindingResult.rejectValue("endChar", "Invalid", "must be a letter");
                return "search";
            }
        }

        // Get matching words and set them on the form
        Collection<String> matchingWords = this.jumbleEngine.searchWords(startCharacter, endCharacter, length);
        form.setWords(matchingWords);

        return "search";
    }

    @GetMapping("subWords")
    public String goGetSubWords(Model model) {
        model.addAttribute("form", new SubWordsForm());
        return "subWords";
    }

    @PostMapping("subWords")
    public String doPostSubWords(
            @ModelAttribute(name = "form") SubWordsForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#generateSubWords()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        if (form.getWord() == null || form.getWord().trim().isEmpty()) {
            bindingResult.rejectValue("word", "NotBlank", "must not be blank");
            return "subWords";
        }

        // Keep original word with spaces for display, but use trimmed version for search
        String originalWord = form.getWord();
        String trimmedWord = originalWord.trim();
        
        // Generate sub words using original input word
        Collection<String> subWords = this.jumbleEngine.generateSubWords(trimmedWord, form.getMinLength());
        
        // Set the original word back on the form (preserving spaces)
        form.setWord(originalWord);
        form.setWords(subWords);
        
        return "subWords";
    }

}
