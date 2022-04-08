package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.service.TokenService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    private final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @GetMapping("/auth")
    public ResponseEntity<Map<String,String>> getToken(@RequestHeader("Authorization") String token){
        String responseValue;
        try{
            responseValue = tokenService.validateToken(token);
        }catch (FeignException.Unauthorized e){
            responseValue="Invalid Token";
            logger.error(e.getMessage());
        }catch (FeignException.BadRequest | JSONException e){
            responseValue="Bad Request";
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,responseValue), HttpStatus.OK);
    }

}
