package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.*;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class QueryRepositoryImpl implements QueryRepository {

    @Value("${saveStoredQuery}")
    private String saveStoredQuery;

    @Value("${fetchStoredQueryJoinQuery}")
    private String fetchStoredQueryJoinQuery;

    @Value("${deletePramRepoQuery}")
    private String deletePramRepoQuery;

    @Value("${deleteStoredQuery}")
    private String deleteStoredQuery;

    @Value("${getNoOfTrendCapturedQueries}")
    private String getNoOfTrendCapturedQueries;

    @Value("${setTrendCaptureQuery}")
    private String setTrendCaptureQuery;

    @Value("${fetchListOfTrendCapturedQueries}")
    private String fetchListOfTrendCapturedQueries;

    @Value("${unsetTrendCaptureQuery}")
    private String unsetTrendCaptureQuery;

    @Value("${getListOfAllTrendCapturedQueriesQuery}")
    private String getListOfAllTrendCapturedQueriesQuery;

    @Value("${saveTrendResultQuery}")
    private String saveTrendResultQuery;

    @Value("${getTrendResultsQuery}")
    private String getTrendResultsQuery;

    @Value("${getListOfTrendCapturedByQueryIdQuery}")
    private String getListOfTrendCapturedByQueryIdQuery;

    @Value("${deleteTrendByTrendIdQuery}")
    private String deleteTrendByTrendIdQuery;


    /**
     * This method will fetch all the stored queries with parameters and repository list of a user using join query
     * @param userId Current user id
     * @return List of stored queries
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public Map<String, Object> getStoredQueries(int userId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(fetchStoredQueryJoinQuery)) {
                preparedStatement.setInt(1,userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String,Object> resultMap = new HashMap<>();
                while (resultSet.next()){
                    if(resultMap.containsKey(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)))){
                        StoredQueryList storedQueryList = (StoredQueryList) resultMap.get(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        storedQueryList.addToQueryParameter(new QueryParameter(resultSet.getString(StringConstants.COLUMN_PARAM_NAME_LABEL),resultSet.getString(StringConstants.COLUMN_PARAM_VALUE_LABEL),resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        storedQueryList.addToQueryRepoList(new QueryRepo(resultSet.getString(StringConstants.COLUMN_NAME_LABEL),resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        resultMap.replace(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)),storedQueryList);
                    }
                    else {
                        StoredQueryList storedQueryList = new StoredQueryList();
                        StoredQuery storedQuery = new StoredQuery();
                        storedQuery.setQueryId(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                        storedQuery.setTitle(resultSet.getString(StringConstants.COLUMN_TITLE_LABEL));
                        storedQuery.setQueryKey(resultSet.getString(StringConstants.COLUMN_QUERY_LABEL));
                        storedQueryList.setStoredQuery(storedQuery);
                        storedQueryList.createQueryParameterList(new QueryParameter(resultSet.getString(StringConstants.COLUMN_PARAM_NAME_LABEL),resultSet.getString(StringConstants.COLUMN_PARAM_VALUE_LABEL),storedQuery.getQueryId()));
                        storedQueryList.createQueryRepoList(new QueryRepo(resultSet.getString(StringConstants.COLUMN_NAME_LABEL),storedQuery.getQueryId()));
                        resultMap.put(String.valueOf(storedQuery.getQueryId()), storedQueryList);
                    }
                }
                return resultMap;
            }
        }
    }

    /**
     * This method will save stored query details in database
     * @param storedQuery stored query details
     * @return the primary key of stored query
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int saveStoredQuery(StoredQuery storedQuery) throws  SQLException {
        try(Connection connection= ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(saveStoredQuery,java.sql.Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setString(1,storedQuery.getTitle());
                preparedStatement.setString(2,storedQuery.getQueryKey());
                preparedStatement.setInt(3,storedQuery.getUserId());
                int returnVal= preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
                else{
                    return returnVal;
                }
            }
        }
    }

    /**
     * This method will delete stored query alongside its parameter and repo name list
     * @param queryId id of query to be deleted
     * @return status of database operation
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int deleteStoredQuery(int queryId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = connection.prepareStatement(deletePramRepoQuery)){
                preparedStatement.setInt(1,queryId);
                int paramRepoVal = preparedStatement.executeUpdate();
                if( paramRepoVal>0){
                    try(PreparedStatement prepared = connection.prepareStatement(deleteStoredQuery)){
                        prepared.setInt(1,queryId);
                        int queryVal = prepared.executeUpdate();
                        if(queryVal>0) {
                            connection.setAutoCommit(true);
                            return queryVal;
                        }
                        else{
                            connection.rollback();
                            return 0;
                        }
                    }
                }else{
                    connection.rollback();
                    return 0;
                }
            }
        }
    }

    /**
     * This method will return number of queries marked for trend capture of current user
     * @param userId Current user id
     * @return count of trend captured queries of current user
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int getTrendCapturedQueryCount(int userId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(getNoOfTrendCapturedQueries, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setInt(1,userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
                else {
                    return 0;
                }
            }
        }
    }

    /**
     * This method will mark the query for trend capture
     * @param queryId id of query to be marked for trend capture
     * @return status of operation
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int setTrendCapture(int queryId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(setTrendCaptureQuery, Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setInt(1,queryId);
                return preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * This method will return list of queries of current user which are marked for trend capture
     * @param userId Current user id
     * @return List of queries
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public Map<String, Object> getListOfTrendCapturedQueries(int userId) throws  SQLException{
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(fetchListOfTrendCapturedQueries)) {
                preparedStatement.setInt(1,userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String,Object> resultMap = new HashMap<>();
                while(resultSet.next()){
                    StoredQuery storedQuery = new StoredQuery();
                    storedQuery.setQueryId(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                    storedQuery.setTitle(resultSet.getString(StringConstants.COLUMN_TITLE_LABEL));
                    storedQuery.setQueryKey(resultSet.getString(StringConstants.COLUMN_QUERY_LABEL));
                    storedQuery.setTrendCaptured(resultSet.getBoolean(StringConstants.COLUMN_IS_TREND_CAPTURED_LABEL));
                    resultMap.put(String.valueOf(storedQuery.getQueryId()), storedQuery);
                }
                return resultMap;
            }
        }
    }

    /**
     * This method will unmark the query from trend capture
     * @param queryId id of query to be unmarked from trend capture
     * @return status of operation
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int unsetTrendCapture(int queryId) throws SQLException{
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(unsetTrendCaptureQuery)){
                preparedStatement.setInt(1,queryId);
                return preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * This method will return list of all the queries marked for trend capture in the stored queries table
     * @return list of queries
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public Map<String, Object> getListOfAllTrendCapturedQueries() throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(getListOfAllTrendCapturedQueriesQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String,Object> resultMap = new HashMap<>();
                while (resultSet.next()){
                    if(resultMap.containsKey(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)))){
                        StoredQueryList storedQueryListOfTrendCapturedQueries = (StoredQueryList) resultMap.get(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        storedQueryListOfTrendCapturedQueries.addToQueryParameter(new QueryParameter(resultSet.getString(StringConstants.COLUMN_PARAM_NAME_LABEL),resultSet.getString(StringConstants.COLUMN_PARAM_VALUE_LABEL),resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        storedQueryListOfTrendCapturedQueries.addToQueryRepoList(new QueryRepo(resultSet.getString(StringConstants.COLUMN_NAME_LABEL),resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)));
                        resultMap.replace(String.valueOf(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL)),storedQueryListOfTrendCapturedQueries);
                    }
                    else {
                        StoredQueryList storedQueryListOfTrendCapturedQueries = new StoredQueryList();
                        StoredQuery storedQuery = new StoredQuery();
                        storedQuery.setQueryId(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                        storedQuery.setTitle(resultSet.getString(StringConstants.COLUMN_TITLE_LABEL));
                        storedQuery.setQueryKey(resultSet.getString(StringConstants.COLUMN_QUERY_LABEL));
                        storedQueryListOfTrendCapturedQueries.setStoredQuery(storedQuery);
                        storedQueryListOfTrendCapturedQueries.setBearerToken(resultSet.getString(StringConstants.COLUMN_BEARER_TOKEN_LABEL));
                        storedQueryListOfTrendCapturedQueries.createQueryParameterList(new QueryParameter(resultSet.getString(StringConstants.COLUMN_PARAM_NAME_LABEL),resultSet.getString(StringConstants.COLUMN_PARAM_VALUE_LABEL),storedQuery.getQueryId()));
                        storedQueryListOfTrendCapturedQueries.createQueryRepoList(new QueryRepo(resultSet.getString(StringConstants.COLUMN_NAME_LABEL),storedQuery.getQueryId()));
                        resultMap.put(String.valueOf(storedQuery.getQueryId()), storedQueryListOfTrendCapturedQueries);
                    }
                }
                return resultMap;
            }
        }
    }

    /**
     * This method will save the result of the query in database for trend capture
     * @param trendCapture trend result with date and queryId
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public void saveTrendResult(TrendCapture trendCapture) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(saveTrendResultQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setDate(1,trendCapture.getDateOfResult());
                preparedStatement.setInt(2,trendCapture.getResult());
                preparedStatement.setInt(3,trendCapture.getQueryId());
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * This method will return the results of all the queries selected by the current user for trend capture
     * @param userId Current user id
     * @return list of results of trend captured queries
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public Map<String, Object> getTrendResults(int userId) throws SQLException{
        try (Connection connection = ConnectionUtil.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(getTrendResultsQuery)) {
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String, List<TrendCapture>> resultMap = new HashMap<>();
                while (resultSet.next()) {
                    if(resultMap.containsKey("QID" + resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL))) {
                        List<TrendCapture> trendCapturesOfQuery = resultMap.get("QID" + resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                        TrendCapture trendCapture = new TrendCapture();
                        trendCapture.setTrendId(resultSet.getInt(StringConstants.COLUMN_TREND_ID_LABEL));
                        trendCapture.setResult(resultSet.getInt(StringConstants.COLUMN_RESULT_LABEL));
                        trendCapture.setDateOfResult(resultSet.getDate(StringConstants.COLUMN_DATE_LABEL));
                        trendCapture.setQueryId(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                        trendCapturesOfQuery.add(trendCapture);
                        resultMap.replace("QID" + resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL),trendCapturesOfQuery);

                    }
                    else {
                        TrendCapture trendCapture = new TrendCapture();
                        trendCapture.setTrendId(resultSet.getInt(StringConstants.COLUMN_TREND_ID_LABEL));
                        trendCapture.setResult(resultSet.getInt(StringConstants.COLUMN_RESULT_LABEL));
                        trendCapture.setDateOfResult(resultSet.getDate(StringConstants.COLUMN_DATE_LABEL));
                        trendCapture.setQueryId(resultSet.getInt(StringConstants.TABLE_QUERYID_LABEL));
                        List<TrendCapture> trendCapturesOfQuery = new ArrayList<>();
                        trendCapturesOfQuery.add(trendCapture);
                        resultMap.put("QID" + trendCapture.getQueryId(), trendCapturesOfQuery);
                    }
                }
                return new HashMap<>(resultMap);
            }
        }
    }

    @Override
    public List<Integer> getListOfTrendCapturedByQueryId(int queryId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(getListOfTrendCapturedByQueryIdQuery)) {
                preparedStatement.setInt(1, queryId);
                ResultSet resultSet = preparedStatement.executeQuery();
                List<Integer> list = new ArrayList<>();
                while (resultSet.next()) {
                    list.add(resultSet.getInt(StringConstants.COLUMN_TREND_ID_LABEL));
                }
                return list;
            }
        }
    }

    @Override
    public void deleteTrendByTrendId(Integer trendId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteTrendByTrendIdQuery)) {
            preparedStatement.setInt(1,trendId);
            preparedStatement.executeUpdate();
            }
        }
    }
}

