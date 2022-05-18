package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.User;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Value("${findByEmailAndPasswordQuery}")
    private String findByEmailAndPasswordQuery;

    @Value("${saveUserQuery}")
    private String saveUserQuery;

    @Value("${updateBearerTokenQuery}")
    private String updateBearerTokenQuery;

    @Override
    public int save(User user) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(saveUserQuery,java.sql.Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getBearerToken());
                preparedStatement.setString(4, user.getPassword());
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

    @Override
    public User findByEmailAndPassword(String email, String password) {
        User user = new User();
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(findByEmailAndPasswordQuery)) {
                preparedStatement.setString(1,email);
                preparedStatement.setString(2,password);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    user.setId(resultSet.getInt("id"));
                    user.setBearerToken(resultSet.getString("bearer_token"));
                    user.setUserName(resultSet.getString("user_name"));
                }
            }
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
        return user;
    }

    @Override
    public int updateToken(int userid,String token) throws SQLException{
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(updateBearerTokenQuery)){
                preparedStatement.setString(1,token);
                preparedStatement.setInt(2,userid);
                return preparedStatement.executeUpdate();
            }
        }
    }
}
