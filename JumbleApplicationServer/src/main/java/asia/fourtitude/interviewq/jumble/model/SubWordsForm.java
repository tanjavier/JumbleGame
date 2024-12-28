package asia.fourtitude.interviewq.jumble.model;

import java.util.Collection;

public class SubWordsForm {

    private String word;

    private Integer minLength;

    private Collection<String> words;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Collection<String> getWords() {
        return words;
    }

    public void setWords(Collection<String> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (word != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("word=[").append(word).append(']');
        }
        if (minLength != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("minLength=[").append(minLength).append(']');
        }
        if (words != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("words=[").append(words).append(']');
        }
        return sb.toString();
    }

}
