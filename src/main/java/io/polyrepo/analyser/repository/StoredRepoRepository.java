package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.RepoNamesList;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public interface StoredRepoRepository {
    void saveRepoNameList(RepoNamesList repoNamesList,int queryId) throws DuplicateKeyException, SQLException;
}
