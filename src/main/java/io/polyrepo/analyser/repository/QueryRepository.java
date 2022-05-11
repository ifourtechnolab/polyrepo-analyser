package io.polyrepo.analyser.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface QueryRepository {

    List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException;
}
