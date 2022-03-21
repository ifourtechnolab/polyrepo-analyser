package io.polyrepo.constant;

import org.springframework.stereotype.Component;

@Component
public class ApplicationConstants {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
