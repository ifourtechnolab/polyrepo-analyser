package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.model.TrendCapture;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Repository
public interface QueryRepository {

    Map<String, Object> getStoredQueries(int userId) throws SQLException;

    int saveStoredQuery(StoredQuery storedQuery)throws SQLException;

    int deleteStoredQuery(int queryId)throws SQLException;

    int getTrendCapturedQueryCount(int userId) throws SQLException;

    int setTrendCapture(int queryId) throws SQLException;

    int unsetTrendCapture(int queryId) throws SQLException;

    Map<String,Object> getTrendResults(int userId) throws SQLException;

    Map<String, Object> getListOfTrendCapturedQueries(int userId) throws SQLException;

    Map<String, Object> getListOfAllTrendCapturedQueries() throws SQLException;

    void saveTrendResult(TrendCapture trendCapture) throws SQLException;

    int setPinned(int queryId) throws SQLException;

    int getPinnedQueryCount(int userId) throws SQLException;

    int unsetPinned(int queryId) throws SQLException;

    int updateStoredQueryWithParameter(int queryId, String title, String paramValue, String paramName) throws SQLException;

    Map<String, Object> getListOfAllPinnedQueries(int userId) throws SQLException;

    List<Integer> getListOfTrendCapturedByQueryId(int queryId) throws SQLException;

    void deleteTrendByTrendId(Integer trendId) throws SQLException;

}
