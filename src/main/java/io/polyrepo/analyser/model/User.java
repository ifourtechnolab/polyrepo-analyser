package io.polyrepo.analyser.model;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", bearerToken='" + bearerToken + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
