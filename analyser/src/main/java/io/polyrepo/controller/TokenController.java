package io.polyrepo.controller;

import io.polyrepo.constant.ApplicationConstants;
import io.polyrepo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polyrepo/analyser")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ApplicationConstants constants;


    @CrossOrigin
    @GetMapping("/auth/{token}")
    public String getToken(@PathVariable String token){

        if(tokenService.validateToken(token)!=null){
            constants.setToken(token);
            return "Valid Token";
        }
        return "Invalid Token";
    }

}
