package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.Label;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.util.QueryUtil;
import org.json.JSONArray;
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
public class LabelService {

    @Value("${getLabelCountQuery}")
    private String getLabelCountQuery;

    @Value("${getLabelsQuery}")
    private String getLabelsQuery;

    @Value("${getRemaininglabelsQuery}")
    private String getRemaininglabelsQuery;

    @Value("${getOpenIssueCountByLabel}")
    private String getOpenIssueCountByLabel;

    @Value("${getOpenIssueNamesByLabel}")
    private String getOpenIssueNamesByLabel;

    @Value("${getOpenIssueCountByLabelWithEndCursor}")
    private String getOpenIssueCountByLabelWithEndCursor;

    @Autowired
    private GraphQLClient client;

    private final Logger logger = LoggerFactory.getLogger(LabelService.class);


    /**
     * This method returns list of labels across selected repositories
     * @param orgUserName   GitHub Organization login name
     * @param token         GitHub personal access token
     * @param repoNamesList List of Repositories selected by user
     * @return List of labels across selected repositories
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object[]> getLabels(String orgUserName, String token, RepoNamesList repoNamesList) throws FeignException, JSONException {
        StringBuilder repoNamesString = QueryUtil.getRepositoryListForQuery(repoNamesList,orgUserName);

        String query = String.format(getLabelsQuery, repoNamesString);
        logger.info("Getting labels of selected repos of organization : {}", orgUserName);
        logger.info("List of selected repositories  : {}", repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONArray nodes = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY).getJSONArray("nodes");


        Set<String> labelNameSet = new TreeSet<>();
        List<Label> labelNameList = new ArrayList<>();

        for (int i = 0; i < nodes.length(); i++){
            JSONArray labels = nodes.getJSONObject(i).getJSONObject(StringConstants.JSON_REPOSITORY_KEY).getJSONArray("labels");
            for (int j = 0; j < labels.length(); j++){
                labelNameSet.add(labels.getJSONObject(j).getString("name").toLowerCase());
            }
        }



        Map<String,String> repoWithCursor = new HashMap<>();
        for (int i = 0; i < nodes.length(); i++){
            if(nodes.getJSONObject(i).getJSONObject(StringConstants.JSON_REPOSITORY_KEY).getJSONObject("pageInfo").getBoolean("hasNextPage")){
                repoWithCursor.put(nodes.getJSONObject(i).getString("name"),nodes.getJSONObject(i).getJSONObject(StringConstants.JSON_REPOSITORY_KEY).getJSONObject("pageInfo").getString("endCursor"));
            }
        }

        logger.info("Repos with labels more than 100 : {}", repoWithCursor.keySet());
        logger.info("Calling getRemainingLabels method to get all remaining labels from above repositories");
        labelNameSet.addAll(getRemainingLabels(orgUserName, token, repoWithCursor));

        for (String s:
                labelNameSet) {
            Label l = new Label();
            l.setName(s);
            labelNameList.add(l);
        }

        logger.info("Number of labels : {}", labelNameList.size());

        return Collections.singletonMap("Labels",labelNameList.toArray());

    }

    /**
     * This method returns list of remaining labels across selected repositories whose labelCount is more than 100
     * It gets the repository name and its respective endCursor from repoWithCursor map
     * @param orgUserName    GitHub Organization login name
     * @param token          GitHub personal access token
     * @param repoWithCursor Map with key as Repository names and value as EndCursor
     * @return List of remaining labels of repositories with labelCount more than 100
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    private Set<String> getRemainingLabels(String orgUserName, String token, Map<String, String> repoWithCursor) throws FeignException, JSONException {

        Set<String> tempLabelNameSet = new HashSet<>();
        ResponseEntity<String> response;

        for (Map.Entry<String,String> entry :
                repoWithCursor.entrySet()) {
            String query = String.format(getRemaininglabelsQuery, orgUserName, entry.getKey(), entry.getValue());
            response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
            JSONArray nodes = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_ORGANIZATION_KEY).getJSONObject("repository").getJSONObject("labels").getJSONArray("nodes");
            for (int j = 0; j < nodes.length(); j++){
                tempLabelNameSet.add(nodes.getJSONObject(j).getString("name").toLowerCase());
            }

        }
        logger.info("Number of remaining labels : {}", tempLabelNameSet.size());
        logger.info("List of remaining labels : {}", tempLabelNameSet);
        return tempLabelNameSet;
    }

    /**
     * This method returns number of open issues related to specific label across selected repositories
     * @param orgUserName   GitHub Organization login name
     * @param label         GitHub Label
     * @param token         GitHub personal access token
     * @param repoNamesList List of Repositories selected by user
     * @return Count of open issues across selected repositories related to a specific label
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public int getOpenIssueCountByLabel(String orgUserName, String label, String token, RepoNamesList repoNamesList) throws FeignException, JSONException {
        StringBuilder repoNamesString = QueryUtil.getRepositoryListForQuery(repoNamesList,orgUserName);

        String query = String.format(getOpenIssueCountByLabel, repoNamesString, label);
        logger.info("Getting number of open issues with label \"{}\" of selected repos of organization : {}", label, orgUserName);
        logger.info("List of selected repositories : {}", repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        return new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY).getInt("issueCount");
    }

    /**
     * This method returns list of open issues related to a specific label across selected repositories
     * if number of issues exceed 100 then the query with pageInfo is executed
     * @param orgUserName   GitHub Organization login name
     * @param label         GitHub Label
     * @param token         GitHub personal access token
     * @param repoNamesList List of Repositories selected by user
     * @return List of open issues across selected repositories related to a specific label
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object> getOpenIssueNamesByLabel(String orgUserName, String label, String token, RepoNamesList repoNamesList) throws FeignException, JSONException {
        StringBuilder repoNamesString = QueryUtil.getRepositoryListForQuery(repoNamesList,orgUserName);

        int issueCount = getOpenIssueCountByLabel(orgUserName, label, token, repoNamesList);
        String query;
        if(issueCount>100){
            query = String.format(getOpenIssueNamesByLabel, repoNamesString, label, " pageInfo{ endCursor, hasNextPage }");
        }
        else{
            query = String.format(getOpenIssueNamesByLabel, repoNamesString, label, " ");
        }
        logger.info("Getting open issues with label \"{}\" of selected repos of organization : {}", label, orgUserName);
        logger.info("List of selected repositories : {}", repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY);
        return result.toMap();
    }

    /**
     * This method returns list of open issues related to a specific label across selected repositories
     * after the end cursor provided
     * @param orgUserName   GitHub Organization login name
     * @param label         GitHub Label
     * @param endCursor     End cursor of previous issue list
     * @param token         GitHub personal access token
     * @param repoNamesList List of Repositories selected by user
     * @return List of open issues across selected repositories related to a specific label
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String, Object> getOpenIssueNamesByLabelWithEndCursor(String orgUserName, String label, String endCursor, String token, RepoNamesList repoNamesList) {
        StringBuilder repoNamesString = QueryUtil.getRepositoryListForQuery(repoNamesList,orgUserName);

        String query = String.format(getOpenIssueCountByLabelWithEndCursor, repoNamesString, label, endCursor, " pageInfo{ endCursor, hasNextPage }");
        logger.info("Getting open issues with label \"{}\" of selected repos of organization : {}", label, orgUserName);
        logger.info("List of selected repositories  : {}", repoNamesList);

        ResponseEntity<String> response;

        response = client.getQuery(StringConstants.AUTH_HEADER_PREFIX + token, query);
        JSONObject result = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY);
        return result.toMap();
    }
}
