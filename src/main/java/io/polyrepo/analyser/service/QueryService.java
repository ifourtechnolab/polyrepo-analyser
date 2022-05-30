package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.*;
import io.polyrepo.analyser.repository.ParameterRepository;
import io.polyrepo.analyser.repository.QueryRepository;
import io.polyrepo.analyser.repository.StoredRepoRepository;
import io.polyrepo.analyser.util.ParameterName;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private StoredRepoRepository storedRepoRepository;

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
     * @param type Type of stored query analysis (pr/issue)
     * @return map with status of database operation
     * @throws SQLException if error occurs in database operation
     */
    public Map<String,Object> saveQueries(StoredQuery storedQuery, RepoNamesList repoNamesList, String orgName, Integer days, String label, String type) throws SQLException{
        logger.info("Saving query in database");
        int storedQueryId = queryRepository.saveStoredQuery(storedQuery);
        if(storedQueryId>0){
            logger.info("Saving parameter in database");
            parameterRepository.saveParameter(new QueryParameter(ParameterName.ORGNAME.getParamName(), orgName,storedQueryId));
            parameterRepository.saveParameter(new QueryParameter(ParameterName.TYPE.getParamName(), type,storedQueryId));
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

    /**
     * This method will get list of all queries marked for trend capture, call getResultForTrend method,
     * check if there are 10 entries saved in database (if yes, delete the oldest entry) and save result in database
     */
    public void scheduledTrendCapture(){
        try {
            logger.debug("Getting List of All Queries Marked for Trend Capture");
            Map<String, Object> listOfAllTrendCapturedQueries = queryRepository.getListOfAllTrendCapturedQueries();
            for (Map.Entry<String, Object> entry :
                    listOfAllTrendCapturedQueries.entrySet()) {
                int result = getResultForTrend((StoredQueryList) entry.getValue());
                TrendCapture trendCapture = new TrendCapture();
                trendCapture.setDateOfResult(Date.valueOf(LocalDate.now()));
                trendCapture.setQueryId(Integer.parseInt(entry.getKey()));
                trendCapture.setResult(result);
                List<Integer> listOfTrendCapturedByQueryId = queryRepository.getListOfTrendCapturedByQueryId(Integer.parseInt(entry.getKey()));
                if(listOfTrendCapturedByQueryId.size() == 10) {
                    queryRepository.deleteTrendByTrendId(listOfTrendCapturedByQueryId.get(0));

                }
                queryRepository.saveTrendResult(trendCapture);

            }
        }
        catch (SQLException e){
            logger.debug(e.getMessage());
        }
    }

    /**
     * This method will get the result of query and return it to scheduledTrendCapture method
     * @param value StoredQueryList object containing queryKey, param list, repoName list
     * @return totalCount (issues or pull requests)
     */
    private int getResultForTrend(StoredQueryList value) {
        List<RepoName> repoNames= new ArrayList<>();
        RepoName repoName = new RepoName();
        for (QueryRepo q:
                value.getQueryRepoList()) {
            repoName.setId(q.getRepoName());
            repoName.setName(q.getRepoName());
            repoNames.add(repoName);
        }
        RepoNamesList repoNamesList = new RepoNamesList();
        repoNamesList.setRepoNames(repoNames);

        String orgName = null;
        int days = 0;
        for (QueryParameter p:
                value.getQueryParameterList()) {
            if(Objects.equals(p.getParamName(), ParameterName.ORGNAME.getParamName())){
                orgName = p.getParamValue();
            }
            if(Objects.equals(p.getParamName(),ParameterName.DAYS.getParamName())){
                days = Integer.parseInt(p.getParamValue());
            }
        }

        Map<String, Object> queryResult = getQueryResult(value.getBearerToken(), value.getStoredQuery().getQueryId()
                , value.getStoredQuery().getQueryKey(), orgName, days, null, repoNamesList);


        if(Objects.equals(value.getStoredQuery().getQueryKey(), "getPriority1IssuesOpenedBeforeXDaysQuery")){
            return (int) queryResult.get("issueCount");
        }
        else{
            Map<String,Object> search = (Map<String, Object>) queryResult.get("search");
            return (int) search.get("totalPullRequest");
        }
    }
}
