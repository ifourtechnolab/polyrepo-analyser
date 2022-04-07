package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.model.RepoNamesList;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Service
public class PullRequestService {

    @Autowired
    private GraphQLClient client;

    @Value("${getPullRequestNotUpdatedByDaysQuery}")
    private String getPullRequestNotUpdatedByDaysQuery;

    @Value("${getUnMergedPullRequestByDayQuery}")
    private String getUnMergedPullRequestByDayQuery;

    private final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

    /**
     * This method will return the list of pull requests without activity since x days from the selected
     * repositories by user
     *
     * @param token         GitHub personal access token
     * @param orgUserName   GitHub Organization login name
     * @param repoNamesList List of Repositories selected by user
     * @param days          Number of days without activity in pull request
     * @return List of pull requests without activity since x days
     * @throws FeignException
     * @throws JSONException
     */
    public Map<String, Object> getPRNotUpdatedByDays(String token, String orgUserName, RepoNamesList repoNamesList, int days) throws FeignException, JSONException {
        StringBuilder repoNamesString = new StringBuilder();
        for (RepoName r :
                repoNamesList.getRepoNames()) {
            repoNamesString.append("repo:").append(orgUserName).append("/").append(r.getName()).append(" ");
        }
        if (repoNamesList.getRepoNames().isEmpty()) {
            repoNamesString.append("org:").append(orgUserName);
        }

        LocalDate date = LocalDate.now().minusDays(days);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String queryDateString = date.format(formatters);
        LOG.info("Getting list of pull requests without activity since " + queryDateString + " from organization: " + orgUserName);
        LOG.info("List of selected repositories : " + repoNamesList);

        String query = String.format(getPullRequestNotUpdatedByDaysQuery, repoNamesString, queryDateString);
        ResponseEntity<String> response;

        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data");
        return result.toMap();
    }

    /**
     * This method will return list of pull requests which are not merged since x days
     *
     * @param token         GitHub personal access token
     * @param orgUserName   GitHub Organization login name
     * @param repoNamesList List of Repositories selected by user
     * @param days          Number of days without merged in pull requests
     * @return List of pull requests which are not merged since x days
     * @throws FeignException
     * @throws JSONException
     */
    public Map<String, Object> getUnMergedPullRequestByDays(String token, String orgUserName, RepoNamesList repoNamesList, int days) throws FeignException, JSONException {
        StringBuilder repoNamesString = new StringBuilder();
        for (RepoName r :
                repoNamesList.getRepoNames()) {
            repoNamesString.append("repo:").append(orgUserName).append("/").append(r.getName()).append(" ");
        }
        if (repoNamesList.getRepoNames().isEmpty()) {
            repoNamesString.append("org:").append(orgUserName);
        }

        LocalDate date = LocalDate.now().minusDays(days);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String queryDateString = date.format(formatters);
        LOG.info("Getting list of pull requests not merged since " + queryDateString + " from organization: " + orgUserName);
        LOG.info("List of selected repositories : " + repoNamesList);

        String query = String.format(getUnMergedPullRequestByDayQuery, repoNamesString, queryDateString);
        ResponseEntity<String> response;

        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data");
        return result.toMap();

    }
}
