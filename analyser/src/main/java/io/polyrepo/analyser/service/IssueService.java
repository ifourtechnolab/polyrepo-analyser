package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.RepoName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class IssueService {

    @Value("${getIssueWithPriority1Query}")
    private String getIssueWithPriority1Query;

    @Value("${getClosedP1IssuesTimeQuery}")
    private String getClosedP1IssuesTimeQuery;

    @Value("${getClosedP2IssuesTimeQuery}")
    private String getClosedP2IssuesTimeQuery;

    @Autowired
    private GraphQLClient client;

    private final Logger LOG = LoggerFactory.getLogger(IssueService.class);

    /**This method returns the list of priority-1 issues of the selected repositories by user
     * and the date they were created
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @param repoNamesList    List of Repositories selected by user
     * @return            List of priority-1 issues open since x date from selected repositories
     */
    public ResponseEntity<?> getIssueWithPriority1(String orgUserName, String token, RepoNamesList repoNamesList) {
        StringBuilder repoNamesString = new StringBuilder();
        for (RepoName r:
             repoNamesList.getRepoNames()) {
            repoNamesString.append("repo:").append(orgUserName).append("/").append(r.getName()).append(" ");
        }
        if(repoNamesList.getRepoNames().isEmpty()){ repoNamesString.append("org:").append(orgUserName);}

        String query = String.format(getIssueWithPriority1Query,repoNamesString);
        LOG.info("Getting priority-1 issues from selected repositories of organization : "+orgUserName);
        LOG.info("List of selected repositories : "+repoNamesList);

        ResponseEntity<String> response;
        try {
            response = client.getQuery("Bearer " + token, query);
            JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
            return new ResponseEntity<>(result.toMap(), HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Bad Request"),HttpStatus.BAD_REQUEST);
        }
    }
}
