package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.QueryParameter;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class ParameterRepositoryImpl implements ParameterRepository{

    @Value("${saveParameterQuery}")
    private String saveParameterQuery;

    /**
     * This method will store parameter of query in database
     * @param queryParameter query parameter details
     * @throws DuplicateKeyException if data with same primary key exists in database
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public void saveParameter(QueryParameter queryParameter) throws DuplicateKeyException, SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(saveParameterQuery)){
                preparedStatement.setString(1,queryParameter.getParamName());
                preparedStatement.setString(2,queryParameter.getParamValue());
                preparedStatement.setInt(3,queryParameter.getQueryId());
                preparedStatement.executeUpdate();
            }
        }
    }
}
