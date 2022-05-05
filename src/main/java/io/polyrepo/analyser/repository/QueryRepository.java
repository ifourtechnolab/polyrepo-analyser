package io.polyrepo.analyser.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QueryRepository implements IQueryRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException {
        String sql = String.format("SELECT s.q_id, title, query, GROUP_CONCAT(name) as RepoNames, param1,param2\n" +
                "FROM stored_queries as s, stored_repository_names\n" +
                "WHERE s.q_id = '%1$s'",userId);
        return  jdbcTemplate.queryForList(sql);
    }
}
