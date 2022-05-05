package io.polyrepo.analyser.service;

import io.polyrepo.analyser.model.User;
import io.polyrepo.analyser.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void save(User user) throws DuplicateKeyException {
        userRepository.save(user);
    }

    public Map<String, Object> login(String email, String password)  throws IndexOutOfBoundsException{
        List<Map<String, Object>> userDetails = userRepository.findByEmailAndPassword(email, password);
        logger.info("User Info: {}",userDetails);
        return userDetails.get(0);
    }
}
