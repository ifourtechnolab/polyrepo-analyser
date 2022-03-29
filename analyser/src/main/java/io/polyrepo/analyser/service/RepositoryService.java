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
public class RepositoryService {


    @Autowired
    private GraphQLClient client;

    private final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);

    /**
     * Returns the total number of repositories the organization has
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return            Total count of repositories of specified organization
     */
    private int getRepositoryCount(String orgUserName, String token) {
        String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories{ totalCount } } }\"}";
        ResponseEntity<String> response = client.getQuery("Bearer " + token, query);
        return new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }

    /** This method returns the repository list according to the number of repositories of the organization
     *  if the total number of repositories is greater than a hundred then
     *  the query with pagination will get executed and return the list of repositories with pagination
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return            List of repositories of specified organization
     */
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
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method fetches the continuing list of repositories of the specified organization using the end cursor of the
     * previous list of repositories and returns the fetched list with pagination
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param endCursor   End cursor of repository list json
     * @return            List of Repository of specified organization with pagination
     */
    public ResponseEntity<?> getRepositoriesByCursor(String orgUserName, String token, String endCursor) {
        String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: 100,after:\\\"" + endCursor + "\\\") { edges { repository:node { name } }pageInfo{endCursor, hasNextPage} } } }\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method fetches the list of repositories which are having the same name as the mentioned name
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param repoName    Repository name
     * @return            List of repository of specified organization having the same name as specified name
     */
    public ResponseEntity<?> getRepositoriesByName(String orgUserName, String token, String repoName) {
        String query = "{\"query\":\"query {search(query: \\\"is:public org:" + orgUserName + " " + repoName + " in:name\\\", type: REPOSITORY, first: 30) {edges {node {... on Repository {name}}}}}\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method with returns the default branch of the specified repository of the organization
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param repoName    Repository name
     * @return            Default branch of specified repository
     */
    public ResponseEntity<?> getDefaultBranchOfRepo(String orgUserName, String token, String repoName) {
        String query = "{\"query\":\"query{repository(owner: \\\""+orgUserName+"\\\", name: \\\""+repoName+"\\\") {defaultBranchRef {defaultBranch :name}}}\"}";
        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("repository");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("edges", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }
}
