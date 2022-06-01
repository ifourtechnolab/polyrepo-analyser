package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.util.AverageUtil;
import io.polyrepo.analyser.util.DateUtil;
import io.polyrepo.analyser.util.QueryUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * and the date they were created with the name of repository they belong to
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
        StringBuilder repoNamesString = QueryUtil.getRepositoryListForQuery(repoNamesList,orgUserName);

        String queryDateString = DateUtil.calculateDateFromDays(days);
        String query = String.format(getPriority1IssuesOpenedBeforeXDaysQuery, repoNamesString, queryDateString);
        logger.info("Getting priority-1 issues from selected repositories open since {}  from organization: {}", queryDateString, orgUserName);
        logger.info("List of selected repositories : {}", repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY);
        result.put(StringConstants.JSON_QUERYKEY_KEY,StringConstants.GRAPHQL_PRIORITY_1_OPEN_ISSUE_QUERY_KEY);
        return result.toMap();
    }

    /**
     * This method returns the average time taken to resolve priority-1 issues
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return Average time taken to resolve priority-1 issues
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, String> getAverageResolvingTimeOfP1Issues(String orgUserName, String token) throws FeignException, JSONException {
        String query = String.format(getClosedP1IssuesTimeQuery, orgUserName);
        logger.info("Getting creation and closing time of priority-1 issues of organization : {}", orgUserName);

        ResponseEntity<String> response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        Map<String,String> result = new HashMap<>(AverageUtil.getAverageTime(response));
        result.put(StringConstants.JSON_QUERYKEY_KEY,StringConstants.GRAPHQL_P1_ISSUE_TIME_QUERY_KEY);
        return result;
    }

    /**
     * This method returns the average time taken to resolve priority-2 issues
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return Average time taken to resolve priority-2 issues
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, String> getAverageResolvingTimeOfP2Issues(String orgUserName, String token) throws FeignException, JSONException {
        String query = String.format(getClosedP2IssuesTimeQuery, orgUserName);
        logger.info("Getting creation and closing time of priority-2 issues of organization : {}", orgUserName);

        ResponseEntity<String> response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        Map<String,String> result = new HashMap<>(AverageUtil.getAverageTime(response));
        result.put(StringConstants.JSON_QUERYKEY_KEY,StringConstants.GRAPHQL_P2_ISSUE_TIME_QUERY_KEY);
        return result;
    }
}

