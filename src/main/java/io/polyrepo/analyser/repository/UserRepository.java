package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;


@Repository
public interface UserRepository {
    int save(User user) throws SQLException;

    User findByEmailAndPassword(String email, String password) throws IndexOutOfBoundsException;

    int updateToken(int userId, String token) throws SQLException;
}
