package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class TokenService {

    @Autowired
    private GraphQLClient client;

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    public ResponseEntity<?> validateToken(String bearerToken){
        String responseValue = "";
        try {
            String query ="{\"query\":\"query { viewer{ login } }\"}";
            ResponseEntity<String> responseEntity = client.getQuery("Bearer " + bearerToken, query);
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
                responseValue="Valid Token";
            }
        }catch (FeignException.Unauthorized e){
            responseValue="Invalid Token";
            LOG.error(e.getMessage());
        }catch (FeignException.BadRequest e){
            responseValue="Bad Request";
            LOG.error(e.getMessage());
        }
        return new ResponseEntity(Collections.singletonMap("message",responseValue), HttpStatus.OK);
    }
}
