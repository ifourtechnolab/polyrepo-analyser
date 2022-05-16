package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.User;
import io.polyrepo.analyser.service.UserService;
import org.json.JSONException;
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

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.save(new User(user.getUserName(), user.getEmail(), user.getBearerToken(), user.getPassword())), HttpStatus.OK);
        } catch (DuplicateKeyException | SQLIntegrityConstraintViolationException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, "Email Already Used"), HttpStatus.OK);
        } catch (FeignException.Unauthorized e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Invalid Token"),HttpStatus.OK);
        }catch (FeignException.BadRequest | JSONException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Bad Request"),HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, e.getMessage()), HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.login(user.getEmail(), user.getPassword()), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, "User Not Found"), HttpStatus.OK);
        }
    }

    @PostMapping("/updateToken")
    public ResponseEntity<Map<String, Object>> updateToken(@RequestBody User user) {
        try{
        return new ResponseEntity<>(userService.updateToken(user), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Invalid Token"),HttpStatus.OK);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Bad Request"),HttpStatus.OK);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, e.getMessage()), HttpStatus.OK);
        }

    }
}
