package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.util.ConnectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class StoredRepoRepositoryImpl implements StoredRepoRepository{

    @Value("${saveStoredRepoQuery}")
    private String saveStoredRepoQuery;

    @Value("${deleteStoredQueryRepoNamesQuery}")
    private String deleteStoredQueryRepoNamesQuery;

    /**
     * This method will store the repositories for query in database
     * @param repoNamesList List of Repositories selected by user
     * @param queryId Query id for query reference
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public void saveRepoNameList(RepoNamesList repoNamesList,int queryId) throws SQLException {
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

    /**
     * This method will update the list of repository names by deleting existing list and adding a new list
     * @param queryId id of query which repository list will be updated
     * @param repoNamesList List of Repositories selected by user
     * @return status of database operation
     * @throws SQLException if error occurs in database operation
     */
    @Override
    public int updateRepoNameList(int queryId, RepoNamesList repoNamesList) throws SQLException {
        try(Connection connection = ConnectionUtil.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteStoredQueryRepoNamesQuery)){
                preparedStatement.setInt(1,queryId);
                int deleteVal = preparedStatement.executeUpdate();
                if(deleteVal>0){
                    for (RepoName repo : repoNamesList.getRepoNames()) {
                        try(PreparedStatement prepStatement = connection.prepareStatement(saveStoredRepoQuery)){
                            prepStatement.setString(1,repo.getName());
                            prepStatement.setInt(2,queryId);
                            prepStatement.executeUpdate();
                        }
                    }
                    connection.setAutoCommit(true);
                    return deleteVal;
                }
                else{
                    connection.rollback();
                    return 0;
                }
            }
        }
    }
}
