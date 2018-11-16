package com.amsavarthan.trivia.models;

public class BooleanQuestion {

    private String response_code,category,type,difficulty,question,correct_answer,ans_1;

    public BooleanQuestion(String response_code, String category, String type, String difficulty, String question, String correct_answer, String ans_1) {
        this.response_code = response_code;
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.correct_answer = correct_answer;
        this.ans_1 = ans_1;
    }

    public String getAns_1() {
        return ans_1;
    }

    public void setAns_1(String ans_1) {
        this.ans_1 = ans_1;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

}
