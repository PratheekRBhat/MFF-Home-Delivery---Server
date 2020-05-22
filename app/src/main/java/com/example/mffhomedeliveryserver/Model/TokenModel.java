package com.example.mffhomedeliveryserver.Model;

public class TokenModel {
    String number, token;

    public TokenModel() {
    }

    public TokenModel(String number, String token) {
        this.number = number;
        this.token = token;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
