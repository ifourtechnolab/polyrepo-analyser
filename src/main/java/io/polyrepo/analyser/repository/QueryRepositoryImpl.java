package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class QueryRepositoryImpl implements QueryRepository {


    @Value("${saveStoredQuery}")
    private String saveStoredQuery;


    @Override
    public Map<String, Object> getStoredQueries(int userId) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(fetchStoredQueryJoinQuery)) {
                preparedStatement.setInt(1,userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String,Object> resultMap = new HashMap<>();
                while (resultSet.next()){
                    if(resultMap.containsKey(String.valueOf(resultSet.getInt("q_id")))){
                        StoredQueryList storedQueryList = (StoredQueryList) resultMap.get(String.valueOf(resultSet.getInt("q_id")));
                        storedQueryList.addToQueryParameter(new QueryParameter(resultSet.getString("param_name"),resultSet.getString("param_value"),resultSet.getInt("q_id")));
                        storedQueryList.addToQueryRepoList(new QueryRepo(resultSet.getString("name"),resultSet.getInt("q_id")));
                        resultMap.replace(String.valueOf(resultSet.getInt("q_id")),storedQueryList);
                    }
                    else {
                        StoredQueryList storedQueryList = new StoredQueryList();
                        StoredQuery storedQuery = new StoredQuery();
                        storedQuery.setId(resultSet.getInt("q_id"));
                        storedQuery.setTitle(resultSet.getString("title"));
                        storedQuery.setQueryKey(resultSet.getString("query"));
                        storedQueryList.setStoredQuery(storedQuery);
                        storedQueryList.createQueryParameterList(new QueryParameter(resultSet.getString("param_name"),resultSet.getString("param_value"),storedQuery.getId()));
                        storedQueryList.createQueryRepoList(new QueryRepo(resultSet.getString("name"),storedQuery.getId()));
                        resultMap.put(String.valueOf(storedQuery.getId()), storedQueryList);
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
}
