package com.hawk.contact.model;

/**
 * Created by Administrator on 2016/3/8.
 */
public class ContactAccount {
    private final String username;
    private final String password;

    private String authToken;
    private String authTokenType;

    public ContactAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthTokenType() {
        return authTokenType;
    }

    public void setAuthTokenType(String authTokenType) {
        this.authTokenType = authTokenType;
    }
}
