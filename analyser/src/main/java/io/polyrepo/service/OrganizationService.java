package io.polyrepo.service;

import feign.FeignException;
import io.polyrepo.client.GraphQLClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OrganizationService {

    @Autowired
    private GraphQLClient client;


    public ResponseEntity<?> getOrganization(String name, String token) {
        String query = "{\"query\":\"query { search(query : \\\"is:public " + name + " is:name type:org\\\" type : USER first : 15){ edges{ node{ ... on Organization{ name login } } } } }\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest e){
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges","Bad Request"),HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> getOrganizationProfile(String orgName, String token) {
        String query = "{\"query\":\" query{organization(login: \\\""+orgName+"\\\") {name, url, avatarUrl}} \"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest e){
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges","Bad Request"),HttpStatus.BAD_REQUEST);
        }
    }
}
