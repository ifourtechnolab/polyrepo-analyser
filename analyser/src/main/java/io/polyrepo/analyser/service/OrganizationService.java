package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OrganizationService {

    @Autowired
    private GraphQLClient client;

    private final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

    /**
     * This method with fetch and returns the list of organizations that have the same name as mentioned name
     * @param name   GitHub Organization login name
     * @param token  GitHub personal access token
     * @return       List of organization
     */
    public ResponseEntity<?> getOrganizationList(String name, String token) {
        String query = "{\"query\":\"query { search(query : \\\"is:public " + name + " is:name type:org\\\" type : USER first : 15){ edges{ node{ ... on Organization{ name login } } } } }\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest | JSONException e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges","Bad Request"),HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method will fetch the name, url and avatar image url of the specified organization
     * @param orgName GitHub Organization login name
     * @param token   GitHub personal access token
     * @return        Profile details of specified organization
     */
    public ResponseEntity<?> getOrganizationProfile(String orgName, String token) {
        String query = "{\"query\":\" query{organization(login: \\\""+orgName+"\\\") {name, url, avatarUrl}} \"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest | JSONException e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges","Bad Request"),HttpStatus.BAD_REQUEST);
        }
    }
}
