package io.polyrepo.analyser.util;

import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.model.RepoNamesList;

public class QueryUtil {
    private QueryUtil() {
    }

    public static StringBuilder getRepositoryListForQuery(RepoNamesList repoNamesList, String orgUserName) {
        StringBuilder repoNamesString = new StringBuilder();
        for (RepoName r :
                repoNamesList.getRepoNames()) {
            repoNamesString.append("repo:").append(orgUserName).append("/").append(r.getName()).append(" ");
        }
        if (repoNamesList.getRepoNames().isEmpty()) {
            repoNamesString.append("org:").append(orgUserName);
        }
        return repoNamesString;
    }
}
