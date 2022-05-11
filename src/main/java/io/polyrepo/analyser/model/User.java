package io.polyrepo.analyser.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    private int id;
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
