package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface UserRepository {
    void save(User user) throws DuplicateKeyException;

    List<Map<String, Object>> findByEmailAndPassword(String email, String password) throws IndexOutOfBoundsException;
}
