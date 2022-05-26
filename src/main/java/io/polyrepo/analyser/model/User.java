package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private int userId;
    private String userName;
    private String email;
    private String bearerToken;
    private String password;

    public User(String userName, String email, String bearerToken, String password) {
        this.userName = userName;
        this.email = email;
        this.bearerToken = bearerToken;
        this.password = password;
    }

}
