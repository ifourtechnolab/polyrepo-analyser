package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class QueryRepositoryImpl implements QueryRepository {


    @Value("${saveStoredQuery}")
    private String saveStoredQuery;


    @Override
    public List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException {
        return new ArrayList<>();
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
}
