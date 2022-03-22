package io.polyrepo.service;

import io.polyrepo.client.GraphQLClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private GraphQLClient client;


    public String validateToken(String bearerToken){
        String query ="{\"query\":\"query { viewer{ login } }\"}";
        return client.getQuery("Bearer "+bearerToken,query);
    }
}
