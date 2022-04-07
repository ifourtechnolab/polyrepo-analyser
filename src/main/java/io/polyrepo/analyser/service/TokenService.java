package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private GraphQLClient client;

    @Value("${validateTokenQuery}")
    private String validateTokenQuery;

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    /**
     * Returns the response entity with the GitHub personal access token validation message
     * @param bearerToken GitHub personal access token
     * @return            the validation of personal access token
     * @throws FeignException
     * @throws JSONException
     */
    public String validateToken(String bearerToken) throws FeignException, JSONException{
        String responseValue = "";

            LOG.info("Validating Bearer Token");
            ResponseEntity<String> responseEntity = client.getQuery("Bearer " + bearerToken, validateTokenQuery);
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
                LOG.info("Bearer Token Valid");
                responseValue="Valid Token";
            }
        return responseValue;
    }
}