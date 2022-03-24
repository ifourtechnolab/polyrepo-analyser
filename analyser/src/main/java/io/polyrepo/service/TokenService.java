package io.polyrepo.service;

import feign.Feign;
import feign.FeignException;
import io.polyrepo.client.GraphQLClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private GraphQLClient client;


    public String validateToken(String bearerToken) throws FeignException{
        String responseValue = "";
        try {
            String query ="{\"query\":\"query { viewer{ login } }\"}";
            ResponseEntity<String> responseEntity = client.getQuery("Bearer " + bearerToken, query);
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
                responseValue="Valid Token";
            }
        }catch (FeignException.Unauthorized e){
            e.printStackTrace();
            responseValue="Invalid Token";
        }
        return responseValue;
    }
}
