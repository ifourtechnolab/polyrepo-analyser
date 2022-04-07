package io.polyrepo.analyser.model;

import java.util.ArrayList;
import java.util.List;

public class RepoNamesList {
    private List<RepoName> repoNames = new ArrayList<>();

    public List<RepoName> getRepoNames() {
        return repoNames;
    }

    public void setRepoNames(List<RepoName> repoNames) {
        this.repoNames = repoNames;
    }

    @Override
    public String toString() {
        return "RepoList{" +
                "repoNames=" + repoNames +
                '}';
    }
}
