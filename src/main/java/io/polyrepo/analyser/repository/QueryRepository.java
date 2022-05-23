package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;


@Repository
public interface QueryRepository {

    Map<String, Object> getStoredQueries(int userId) throws SQLException;

    int saveStoredQuery(StoredQuery storedQuery)throws SQLException;

    int deleteStoredQuery(int queryId)throws SQLException;
}
