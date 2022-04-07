package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RepositoryService {


    @Autowired
    private GraphQLClient client;

    @Value("${getRepositoryCountQuery}")
    private String getRepositoryCountQuery;

    @Value("${getRepositoriesQueryUnder100}")
    private String getRepositoriesQueryUnder100;

    @Value("${getRepositoriesQueryOver100}")
    private String getRepositoriesQueryOver100;

    @Value("${getRepositoriesByCursorQuery}")
    private String getRepositoriesByCursorQuery;

    @Value("${getRepositoriesByNameQuery}")
    private String getRepositoriesByNameQuery;

    private final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);

    /**
     * Returns the total number of repositories the organization has
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return Total count of repositories of specified organization
     */
    private int getRepositoryCount(String orgUserName, String token) {
        String query = String.format(getRepositoryCountQuery, orgUserName);
        ResponseEntity<String> response = client.getQuery("Bearer " + token, query);
        return new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }

    /**
     * This method returns the repository list according to the number of repositories of the organization
     * if the total number of repositories is greater than a hundred then
     * the query with pagination will get executed and return the list of repositories with pagination
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return List of repositories of specified organization
     * @throws FeignException
     * @throws JSONException
     */
    public Map<String, Object> getRepositories(String orgUserName, String token) throws FeignException, JSONException {
        ResponseEntity<String> response;

        int count = getRepositoryCount(orgUserName, token);
        LOG.info("Total count of repositories: " + count);
        if (count <= 100) {
            LOG.info("Getting all the repositories");
            String query = String.format(getRepositoriesQueryUnder100, orgUserName, count);
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
            return result.toMap();
        } else {
            LOG.info("Getting first 100 repositories");
            String query = String.format(getRepositoriesQueryOver100, orgUserName);
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
            return result.toMap();
        }
    }

    /**
     * This method fetches the continuing list of repositories of the specified organization using the end cursor of the
     * previous list of repositories and returns the fetched list with pagination
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param endCursor   End cursor of repository list json
     * @return List of Repository of specified organization with pagination
     * @throws FeignException
     * @throws JSONException
     */
    public Map<String, Object> getRepositoriesByCursor(String orgUserName, String token, String endCursor) throws FeignException, JSONException {
        String query = String.format(getRepositoriesByCursorQuery, orgUserName, endCursor);
        ResponseEntity<String> response;

        LOG.info("Getting 100 repositories after the cursor \"" + endCursor + "\"");
        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("organization").getJSONObject("repositories");
        return result.toMap();
    }

    /**
     * This method fetches the list of repositories which are having the same name as the mentioned name
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param repoName    Repository name
     * @return List of repository of specified organization having the same name as specified name
     * @throws FeignException
     * @throws JSONException
     */
    public Map<String, Object> getRepositoriesByName(String orgUserName, String token, String repoName) throws FeignException, JSONException {
        String query = String.format(getRepositoriesByNameQuery, orgUserName, repoName);
        ResponseEntity<String> response;
        LOG.info("Getting repositories by name : " + repoName);
        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
        return result.toMap();
    }

}
