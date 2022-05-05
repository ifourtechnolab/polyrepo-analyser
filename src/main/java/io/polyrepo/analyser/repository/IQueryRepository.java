package io.polyrepo.analyser.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface IQueryRepository {

    List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException;
}
