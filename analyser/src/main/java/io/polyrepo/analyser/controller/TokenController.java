package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.service.TokenService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    private final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @CrossOrigin
    @GetMapping("/auth")
    public ResponseEntity<?> getToken(@RequestHeader("Authorization") String token){
        String responseValue="";
        try{
            responseValue = tokenService.validateToken(token);
        }catch (FeignException.Unauthorized e){
            responseValue="Invalid Token";
            LOG.error(e.getMessage());
        }catch (FeignException.BadRequest | JSONException e){
            responseValue="Bad Request";
            LOG.error(e.getMessage());
        }
        return new ResponseEntity<>(Collections.singletonMap("message",responseValue), HttpStatus.OK);
    }

}
