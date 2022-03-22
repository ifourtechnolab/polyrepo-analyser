package io.polyrepo.service;

import io.polyrepo.client.GraphQLClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {


    @Autowired
    private GraphQLClient client;

    public String getRepositories(String orgUserName, String token) {
        int count = getRepositoryCount(token);
        String query = "{\"query\":\"query { organization(login: \\\""+orgUserName+"\\\") { repositories(first: " + count + ") { edges { repository:node { name } } } } }\"}";
        return client.getQuery("Bearer "+token, query);
    }

    private int getRepositoryCount(String token) {
        String query = "{\"query\":\"query { organization(login: \\\"signalapp\\\") { repositories{ totalCount } } }\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer "+token, query));
        return response.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }
}
