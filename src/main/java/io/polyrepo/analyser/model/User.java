package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "bearer_token")
    private String bearerToken;

    @Column(name = "password")
    private String password;

    public User(String userName, String email, String bearerToken, String password) {
        this.userName = userName;
        this.email = email;
        this.bearerToken = bearerToken;
        this.password = password;
    }

}
