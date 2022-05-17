package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQueries;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Repository
public interface QueryRepository {

    List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException;

    int saveStoredQuery(StoredQueries storedQueries)throws DuplicateKeyException, SQLException;
}
