package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PullRequestService {

    @Autowired
    private GraphQLClient client;

    @Value("${getPullRequestNotUpdatedByDaysQuery}")
    private String getPullRequestNotUpdatedByDaysQuery;

    private final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

    /**
     * This method will return the list of pull requests without activity since x days from the selected
     * repositories by user
     * @param token         GitHub personal access token
     * @param orgUserName   GitHub Organization login name
     * @param repoList      List of Repositories selected by user
     * @param days          Number of days without activity in pull request
     * @return              List of pull requests without activity since x days
     */
    public ResponseEntity<?> getPRNotUpdatedByDays(String token, String orgUserName, Map<String, List<String>> repoList, int days) {
        StringBuilder queryRepo = new StringBuilder();
        for (String repositoryName : repoList.get("repositories")) {
            queryRepo.append("repo:").append(orgUserName).append("/").append(repositoryName).append(" ");
        }
        LocalDate date = LocalDate.now().minusDays(days);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String queryDate = date.format(formatters);
        LOG.info("Getting list of pull requests without activity since "+queryDate+" from organization: "+orgUserName);

        String query = String.format(getPullRequestNotUpdatedByDaysQuery,queryRepo,queryDate);
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
