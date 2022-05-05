package io.polyrepo.analyser.model;

public class User {

    private int id;
    private String email;
    private String bearerToken;
    private String password;

    public User(String email, String bearerToken, String password) {
        this.email = email;
        this.bearerToken = bearerToken;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", bearerToken='" + bearerToken + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
