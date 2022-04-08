package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.util.DateUtil;
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
public class IssueService {

    @Value("${getPriority1IssuesOpenedBeforeXDaysQuery}")
    private String getPriority1IssuesOpenedBeforeXDaysQuery;

    @Value("${getClosedP1IssuesTimeQuery}")
    private String getClosedP1IssuesTimeQuery;

    @Value("${getClosedP2IssuesTimeQuery}")
    private String getClosedP2IssuesTimeQuery;

    @Autowired
    private GraphQLClient client;

    private final Logger logger = LoggerFactory.getLogger(IssueService.class);

    /**
     * This method returns the list of priority-1 issues of the selected repositories by user
     * and the date they were created
     *
     * @param orgUserName   GitHub Organization login name
     * @param token         GitHub personal access token
     * @param repoNamesList List of Repositories selected by user
     * @param days          Number of days since before priority-1 issues are open
     * @return List of priority-1 issues open since before x date from selected repositories
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object> getPriority1IssuesOpenedBeforeXDays(String orgUserName, String token, RepoNamesList repoNamesList, int days) throws FeignException, JSONException {
        StringBuilder repoNamesString = new StringBuilder();
        for (RepoName r :
                repoNamesList.getRepoNames()) {
            repoNamesString.append("repo:").append(orgUserName).append("/").append(r.getName()).append(" ");
        }
        if (repoNamesList.getRepoNames().isEmpty()) {
            repoNamesString.append("org:").append(orgUserName);
        }

        String queryDateString = DateUtil.calculateDateFromDays(days);
        String query = String.format(getPriority1IssuesOpenedBeforeXDaysQuery, repoNamesString, queryDateString);
        logger.info("Getting priority-1 issues from selected repositories open since " + queryDateString + " from organization: " + orgUserName);
        logger.info("List of selected repositories : " + repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
        return result.toMap();
    }

    /**
     * This method returns the list of priority-1 issue creation and closing time through
     * which average time taken to resolve priority-1 issues can be obtained
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return List of priority-1 issues' creation and closing time
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object> getClosedP1IssuesTime(String orgUserName, String token) throws FeignException, JSONException {
        String query = String.format(getClosedP1IssuesTimeQuery, orgUserName);
        logger.info("Getting creation and closing time of priority-1 issues of organization : " + orgUserName);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
        return result.toMap();

    }

    /**
     * This method returns the list of priority-2 issue creation and closing time through
     * which average time taken to resolve priority-2 issues can be obtained
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return List of priority-2 issues' creation and closing time
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object> getClosedP2IssuesTime(String orgUserName, String token) throws FeignException, JSONException {
        String query = String.format(getClosedP2IssuesTimeQuery, orgUserName);
        logger.info("Getting creation and closing time of priority-2 issues of organization : " + orgUserName);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
        return result.toMap();

    }
}

