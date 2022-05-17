package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Repository
public interface QueryRepository {

    List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException;

    int saveStoredQuery(StoredQuery storedQuery)throws SQLException;
}
