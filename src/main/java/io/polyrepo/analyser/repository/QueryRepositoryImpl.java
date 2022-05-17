package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class QueryRepositoryImpl implements QueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${saveStoredQuery}")
    private String saveStoredQuery;


    @Override
    public List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException {
        String sql = String.format("SELECT s.q_id, title, query, GROUP_CONCAT(name) as RepoNames, param1,param2\n" +
                "FROM stored_queries as s, stored_repository_names\n" +
                "WHERE s.q_id = '%1$s'",userId);
        return  jdbcTemplate.queryForList(sql);
    }

    /**
     * This method will save stored query details in database
     * @param storedQuery stored query details
     * @return the primary key of stored query
     * @throws DuplicateKeyException if data with same primary key exists in database
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int saveStoredQuery(StoredQuery storedQuery) throws DuplicateKeyException, SQLException {
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
}
