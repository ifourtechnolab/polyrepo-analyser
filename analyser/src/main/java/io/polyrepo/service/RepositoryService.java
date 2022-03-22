package io.polyrepo.service;

import io.polyrepo.client.GraphQLClient;
import io.polyrepo.constant.ApplicationConstants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {


    @Autowired
    private GraphQLClient client;

    @Autowired
    private ApplicationConstants constants;

    public String getRepositories(String orgUserName) {
        int count = getRepositoryCount();
        String query = "{\"query\":\"query { organization(login: \\\""+orgUserName+"\\\") { repositories(first: " + count + ") { edges { repository:node { name } } } } }\"}";
        return client.getQuery("Bearer "+constants.getToken(), query);
    }

    private int getRepositoryCount() {
        String query = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repositories{ totalCount } } }\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer "+constants.getToken(), query));
        return response.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }
}
