package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;


@Repository
public class UserRepository implements IUserRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(User user) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO user (email, bearer_token, password) VALUES(?,?,?)",
                    user.getEmail(), user.getBearerToken(), user.getPassword());
    }

    @Override
    public List<Map<String, Object>> findByEmailAndPassword(String email, String password) throws IndexOutOfBoundsException {

        String sql = String.format("SELECT user.id,user.bearer_token FROM user where user.email='%1$s' && user.password='%2$s'",email,password);
        return  jdbcTemplate.queryForList(sql);
    }
}
