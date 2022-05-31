package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.RepoNamesList;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public interface StoredRepoRepository {
    void saveRepoNameList(RepoNamesList repoNamesList,int queryId) throws SQLException;

    int updateRepoNameList(int queryId, RepoNamesList repoNamesList) throws SQLException;
}
