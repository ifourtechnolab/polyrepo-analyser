package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepo extends CrudRepository<User, Integer> {
    User findByEmailAndPassword(String email,String password);

    @Modifying
    @Transactional
    @Query("update User u set u.bearerToken = ?1 where u.userId = ?2")
    int setBearerTokenFor(String bearerToken, int userId);
}
