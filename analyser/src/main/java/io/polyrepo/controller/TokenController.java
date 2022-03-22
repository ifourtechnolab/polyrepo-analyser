package io.polyrepo.controller;

import io.polyrepo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polyrepo/analyser")
public class TokenController {

    @Autowired
    private TokenService tokenService;


    @CrossOrigin
    @GetMapping("/auth")
    public String getToken(@RequestHeader("Authorization") String token){

        if(tokenService.validateToken(token)!=null){
            return "Valid Token";
        }
        return "Invalid Token";
    }

}
