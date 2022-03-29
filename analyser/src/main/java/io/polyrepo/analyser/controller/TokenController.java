package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;


    @CrossOrigin
    @GetMapping("/auth")
    public ResponseEntity<?> getToken(@RequestHeader("Authorization") String token){
        return tokenService.validateToken(token);
    }

}
