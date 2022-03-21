package io.polyrepo.service;

import io.polyrepo.client.TokenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenClient tokenClient;


    public String validateToken(String bearerToken){
        String query ="{\"query\":\"query { viewer{ login } }\"}";
        return tokenClient.validateToken("Bearer "+bearerToken,query);
    }
}
