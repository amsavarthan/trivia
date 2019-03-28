package com.amsavarthan.game.trivia.models;

public class SessionToken {

    String response_code,token;

    public SessionToken(String response_code, String token) {
        this.response_code = response_code;
        this.token = token;
    }

    public String getResult_code() {
        return response_code;
    }

    public void setResult_code(String response_code) {
        this.response_code = response_code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
