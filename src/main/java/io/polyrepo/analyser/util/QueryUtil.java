package io.polyrepo.analyser.util;

import io.polyrepo.analyser.model.QueryRepo;
import io.polyrepo.analyser.model.RepoName;
import io.polyrepo.analyser.model.RepoNamesList;

import java.util.List;

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

    public static StringBuilder getPinnedQuery(List<QueryRepo> queryRepoList,String query,String orgName, String days){
        String queryDate = DateUtil.calculateDateFromDays(Integer.parseInt(days));
        StringBuilder pinnedString = new StringBuilder();
        int index =1;
        for (QueryRepo queryRepo : queryRepoList) {
            pinnedString.append(String.format(query,orgName+index, "repo:"+orgName+"/"+queryRepo.getRepoName(), queryDate));
            index++;
        }
        return pinnedString;
    }
}
