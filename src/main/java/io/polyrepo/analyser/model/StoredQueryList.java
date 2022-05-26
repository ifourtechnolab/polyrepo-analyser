package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StoredQueryList {
    private StoredQuery storedQuery;
    private String bearerToken;
    private List<QueryParameter> queryParameterList;
    private List<QueryRepo> queryRepoList;

    public void createQueryParameterList(QueryParameter queryParameter){
        this.queryParameterList = new ArrayList<>();
        queryParameterList.add(queryParameter);
    }

    public void createQueryRepoList(QueryRepo queryRepo){
        this.queryRepoList = new ArrayList<>();
        queryRepoList.add(queryRepo);
    }

    public void addToQueryParameter(QueryParameter queryParameter){
        if(!this.queryParameterList.contains(queryParameter))
            this.queryParameterList.add(queryParameter);
    }

    public void addToQueryRepoList(QueryRepo queryRepo){
        if(!this.queryRepoList.contains(queryRepo))
            this.queryRepoList.add(queryRepo);
    }
}
