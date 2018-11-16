package com.amsavarthan.trivia.models;

public class Score {

    private String category,correct,incorrect,missed,questions_asked,timestamp;

    public Score(){

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(String incorrect) {
        this.incorrect = incorrect;
    }

    public String getMissed() {
        return missed;
    }

    public void setMissed(String missed) {
        this.missed = missed;
    }

    public String getQuestions_asked() {
        return questions_asked;
    }

    public void setQuestions_asked(String questions_asked) {
        this.questions_asked = questions_asked;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
