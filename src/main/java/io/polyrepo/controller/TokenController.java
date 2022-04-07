package io.polyrepo.controller;

import io.polyrepo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/polyrepo/analyser")
public class TokenController {

    @Autowired
    private TokenService tokenService;


    @CrossOrigin
    @GetMapping("/auth")
    public ResponseEntity<?> getToken(@RequestHeader("Authorization") String token){
        return new ResponseEntity(Collections.singletonMap("response",tokenService.validateToken(token)), HttpStatus.OK);
    }

}
