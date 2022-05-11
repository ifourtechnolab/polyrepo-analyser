package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.User;
import io.polyrepo.analyser.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> createUser(@RequestBody User user) {
        try {
            userService.save(new User(user.getUserName(), user.getEmail(), user.getBearerToken(), user.getPassword()));
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"User was created successfully."), HttpStatus.OK);
        } catch (DuplicateKeyException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Email Already Used"), HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,e.getMessage()), HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody User user){
        try{
            Map<String, Object> response = userService.login(user.getEmail(), user.getPassword());
            response.put(StringConstants.JSON_MESSAGE_KEY_STRING, "User Found");
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (IndexOutOfBoundsException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, "User Not Found"), HttpStatus.OK);
        }
    }

}
