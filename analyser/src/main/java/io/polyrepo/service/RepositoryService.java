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
public class RepositoryService {


    @Autowired
    private GraphQLClient client;

    private int getRepositoryCount(String orgUserName, String token) {
        String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories{ totalCount } } }\"}";
        ResponseEntity<String> response = client.getQuery("Bearer " + token, query);
        return new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }

    public ResponseEntity<?> getRepositories(String orgUserName, String token) {
        ResponseEntity<String> response;
        try {
            int count = getRepositoryCount(orgUserName, token);
            if (count <= 100) {
                String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: " + count + ") { edges { repository:node { name } } } } }\"}";
                response = client.getQuery("Bearer " + token, query);
                JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
                return new ResponseEntity<>(result.toMap(), HttpStatus.OK);

            } else {
                String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: 100) { edges { repository:node { name } }pageInfo{endCursor, hasNextPage} } } }\"}";
                response = client.getQuery("Bearer " + token, query);
                JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
                return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
            }
        } catch (FeignException.Unauthorized e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getRepositoriesByCursor(String orgUserName, String token, String endCursor) {
        String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: 100,after:\\\"" + endCursor + "\\\") { edges { repository:node { name } }pageInfo{endCursor, hasNextPage} } } }\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getRepositoriesByName(String orgUserName, String token, String repoName) {
        String query = "{\"query\":\"query {search(query: \\\"is:public org:" + orgUserName + " " + repoName + " in:name\\\", type: REPOSITORY, first: 30) {edges {node {... on Repository {name}}}}}\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }
}
