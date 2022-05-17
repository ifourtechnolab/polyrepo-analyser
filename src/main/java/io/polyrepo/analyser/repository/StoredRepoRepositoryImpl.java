package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class StoredRepoRepositoryImpl implements StoredRepoRepository{

    @Value("${saveStoredRepoQuery}")
    private String saveStoredRepoQuery;

    /**
     * This method will store the repositories for query in database
     * @param repoNamesList List of Repositories selected by user
     * @param queryId Query id for query reference
     * @throws DuplicateKeyException if data with same primary key exists in database
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public void saveRepoNameList(RepoNamesList repoNamesList,int queryId) throws DuplicateKeyException, SQLException {
        try(Connection connection = ConnectionUtil.getConnection()){
            for (RepoName repo : repoNamesList.getRepoNames()) {
                try(PreparedStatement preparedStatement = connection.prepareStatement(saveStoredRepoQuery)){
                    preparedStatement.setString(1,repo.getName());
                    preparedStatement.setInt(2,queryId);
                    preparedStatement.executeUpdate();
                }
            }
        }

    }
}
