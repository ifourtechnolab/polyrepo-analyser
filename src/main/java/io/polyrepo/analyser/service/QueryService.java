package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.QueryParameter;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.repository.ParameterRepository;
import io.polyrepo.analyser.repository.QueryRepository;
import io.polyrepo.analyser.repository.StoredRepoRepository;
import io.polyrepo.analyser.util.ParameterName;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    StoredRepoRepository storedRepoRepository;

    @Autowired
    private IssueService issueService;

    @Autowired
    private PullRequestService pullRequestService;

    @Autowired
    private LabelService labelService;

    private final Logger logger = LoggerFactory.getLogger(QueryService.class);

    /**
     * This method get all the details of stored queries of current user
     * @param userId Current user id
     * @return Map with the details of stored queries
     * @throws SQLException if error occurs in database operation
     */
    public Map<String, Object> getStoredQueries(int userId) throws SQLException {
        return queryRepository.getStoredQueries(userId);
    }

    /**
     * This method will call repository to save query, parameter and repolist in database
     * @param storedQuery Save query details
     * @param repoNamesList List of Repositories selected by user
     * @param orgName GitHub Organization login name
     * @param days Number of days for filter
     * @return map with status of database operation
     * @throws SQLException if error occurs in database operation
     */
    public Map<String,Object> saveQueries(StoredQuery storedQuery, RepoNamesList repoNamesList, String orgName, Integer days, String label) throws SQLException{
        logger.info("Saving query in database");
        int storedQueryId = queryRepository.saveStoredQuery(storedQuery);
        if(storedQueryId>0){
            logger.info("Saving parameter in database");
            parameterRepository.saveParameter(new QueryParameter(ParameterName.ORGNAME.getParamName(), orgName,storedQueryId));
            if(days!=null){
                parameterRepository.saveParameter(new QueryParameter(ParameterName.DAYS.getParamName(), days.toString(),storedQueryId));
            }
            if(label!=null){
                parameterRepository.saveParameter(new QueryParameter(ParameterName.LABEL.getParamName(), label,storedQueryId));
            }
            if(repoNamesList!=null){
                logger.info("Saving Repolist in database");
                storedRepoRepository.saveRepoNameList(repoNamesList,storedQueryId);
            }
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query saved successfully");
        }
        else{
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query not saved");
        }
    }

    /**
     * This method will delete the stored query by id
     * @param queryId id of query to be deleted
     * @return stored query delete status message
     * @throws SQLException if error occurs in database operation
     */
    public Map<String,String> deleteQuery(int queryId) throws SQLException{
        int returnVal = queryRepository.deleteStoredQuery(queryId);
        if(returnVal>0){
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query deleted");
        }else{
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query not deleted");
        }
    }

    /**
     * This method will call the service of analysis according to the query key
     * @param token GitHub personal access token
     * @param queryId Query key to identify the analysis
     * @param queryKey Query key to identify the analysis
     * @param orgUserName GitHub Organization login name
     * @param days Number of days for filter
     * @param label  Label for filter
     * @param repoNamesList List of Repositories selected by user
     * @return result of stored query from the service
     * @throws FeignException FeignException.Unauthorized if token is invalid, FeignException.BadRequest if FeignClient returns 400 Bad Request
     * @throws JSONException if JSON parsing is invalid
     */
    public Map<String,Object> getQueryResult(String token, int queryId, String queryKey, String orgUserName, Integer days, String label, RepoNamesList repoNamesList) throws FeignException, JSONException {
        switch (queryKey){
            case "getPullRequestNotUpdatedByDaysQuery":
                return pullRequestService.getPRNotUpdatedByDays(token,orgUserName,repoNamesList,days);
            case "getUnMergedPullRequestByDayQuery":
                return pullRequestService.getUnMergedPullRequestByDays(token,orgUserName,repoNamesList,days);
            case "getPriority1IssuesOpenedBeforeXDaysQuery":
                return issueService.getPriority1IssuesOpenedBeforeXDays(orgUserName,token,repoNamesList,days);
            case "getOpenIssueNamesByLabel":
                return labelService.getOpenIssueNamesByLabel(orgUserName,label,token,repoNamesList);
            case "getClosedP1IssuesTimeQuery":
                return new HashMap<>(issueService.getAverageResolvingTimeOfP1Issues(orgUserName,token));
            case "getClosedP2IssuesTimeQuery":
                return new HashMap<>(issueService.getAverageResolvingTimeOfP2Issues(orgUserName,token));
            default:
                return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Data not found");
        }
    }

    /**
     * This method will call the repository to check if current user has three queries marked for trend capture
     * if not then it will call repository method to mark given queryId for trend capture
     * @param userId Current user id
     * @param queryId id of query to be marked for trend capture
     * @return status of operation
     * @throws SQLException if error occurs in database operation
     */
    public Map<String,Object> setTrendCapture(int userId, int queryId) throws SQLException {
        int count = queryRepository.getTrendCapturedQueryCount(userId);
        if(count==3){
            logger.info("Three Queries Already Marked For Trend Capture");
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Three Queries Already Marked For Trend Capture");
        }
        else {
            logger.info("Setting Query For Trend Capture");
            if(queryRepository.setTrendCapture(queryId)>0) {
                return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, "Query Set For Trend Capture");
            }
            else {
                return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, "Query Not Set For Trend Capture");
            }
        }
    }

    /**
     * This method will call the repository method to return list of queries of current user that are marked for trend capture
     * @param userId Current user id
     * @return List of queries marked for trend capture of current user
     * @throws SQLException if error occurs in database operation
     */
    public Map<String, Object> getListOfTrendCapturedQueries(int userId) throws SQLException {
        return queryRepository.getListOfTrendCapturedQueries(userId);
    }

    /**
     *This method will call the repository method for un-marking the query from trend capture
     * @param queryId id of query to be unmarked from trend capture
     * @return status of operation
     * @throws SQLException if error occurs in database operation
     */
    public Map<String, Object> unsetTrendCapture(int queryId) throws SQLException{
        int returnVal = queryRepository.unsetTrendCapture(queryId);
        if(returnVal>0){
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query Removed from Trend Capture");
        }else {
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query Not Removed from Trend Capture");
        }
    }

    /**
     * This method will call repository method to return list of trend results for database
     * @param userId Current user id
     * @return List of trend result
     * @throws SQLException if error occurs in database operation
     */
    public Map<String, Object> getTrendResults(int userId) throws SQLException{
        return queryRepository.getTrendResults(userId);
    }
}
