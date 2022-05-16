package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.User;
import io.polyrepo.analyser.repository.UserRepository;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Map<String, Object> save(User user) throws DuplicateKeyException, SQLException, FeignException, JSONException {
        Map<String, Object> response = new HashMap<>();
        String responseValue;
        responseValue = tokenService.validateToken(user.getBearerToken());
        if (responseValue.equals("Valid Token")) {
            int returnVal = userRepository.save(user);
            if (returnVal > 0) {
                if (returnVal > 0) {
                    response.put(StringConstants.JSON_MESSAGE_KEY_STRING, "User was created successfully.");
                    response.put("id", returnVal);
                    response.put("bearer_token", user.getBearerToken());
                } else {
                    response.put(StringConstants.JSON_MESSAGE_KEY_STRING, "User creation process failed");
                }
            }
        }
        return response;


    }

    public Map<String, Object> updateToken(User user) throws FeignException, JSONException, SQLException {
        Map<String, Object> response = new HashMap<>();
        String responseValue;
        responseValue = tokenService.validateToken(user.getBearerToken());
        if (responseValue.equals("Valid Token")) {
            int returnValue = userRepository.updateToken(user.getId(), user.getBearerToken());
            if (returnValue > 0) {
                response.put(StringConstants.JSON_MESSAGE_KEY_STRING, "Token updated");
            } else {
                response.put(StringConstants.JSON_MESSAGE_KEY_STRING, "Token update process failed");
            }
        }
        return response;
    }

    public Map<String, Object> login(String email, String password) throws IndexOutOfBoundsException {
        User user = userRepository.findByEmailAndPassword(email, password);
        Map<String, Object> userDetail = new HashMap<>();
        if (user.getBearerToken() != null) {
            userDetail.put("id", user.getId());
            userDetail.put("bearer_token", user.getBearerToken());
            userDetail.put("user_name",user.getUserName());
            String responseValue;
            try {
                responseValue = tokenService.validateToken(user.getBearerToken());
            } catch (FeignException.Unauthorized e) {
                responseValue = "Invalid Token";
                logger.error(e.getMessage());
            } catch (FeignException.BadRequest | JSONException e) {
                responseValue = "Bad Request";
                logger.error(e.getMessage());
            }
            userDetail.put("token_validation", responseValue);
            userDetail.put(StringConstants.JSON_MESSAGE_KEY_STRING, "User Found");
            logger.info("User Info: {} , {}", user.getId(), user.getBearerToken());
        } else {
            userDetail.put(StringConstants.JSON_MESSAGE_KEY_STRING, "User Not Found");
        }
        return userDetail;
    }
}