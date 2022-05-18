package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QueryRepo {
    private int id;
    private String repoName;
    private int queryId;

    public QueryRepo(String repoName, int queryId) {
        this.repoName = repoName;
        this.queryId = queryId;
    }
}
