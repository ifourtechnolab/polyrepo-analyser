package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;


@Repository
public class UserRepository implements IUserRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${findByEmailAndPasswordQuery}")
    private String findByEmailAndPasswordQuery;

    @Value("${saveUserQuery}")
    private String saveUserQuery;

    @Override
    public void save(User user) throws DuplicateKeyException {
        jdbcTemplate.update(saveUserQuery,
                    user.getUserName(), user.getEmail(), user.getBearerToken(), user.getPassword());
    }

    @Override
    public List<Map<String, Object>> findByEmailAndPassword(String email, String password) throws IndexOutOfBoundsException {
        String sql = String.format(findByEmailAndPasswordQuery,email,password);
        return  jdbcTemplate.queryForList(sql);
    }
}
