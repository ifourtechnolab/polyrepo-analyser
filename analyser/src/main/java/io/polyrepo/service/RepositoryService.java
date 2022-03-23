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
        int count = getRepositoryCount(orgUserName,token);
        if(count<=100) {
            String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: " + count + ") { edges { repository:node { name } } } } }\"}";
            JSONObject response = new JSONObject(client.getQuery("Bearer " + token, query));
            return response.getJSONObject("data").getJSONObject("organization").toString();
        }
        else{
            String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: 100) { edges { repository:node { name } }pageInfo{endCursor, hasNextPage} } } }\"}";
            JSONObject response = new JSONObject(client.getQuery("Bearer " + token, query));
            return response.getJSONObject("data").getJSONObject("organization").toString();
        }
    }

    private int getRepositoryCount(String orgUserName, String token) {
        String query = "{\"query\":\"query { organization(login: \\\""+orgUserName+"\\\") { repositories{ totalCount } } }\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer "+token, query));
        return response.getJSONObject("data").getJSONObject("organization").getJSONObject("repositories").getInt("totalCount");
    }

    public String getRepositoriesByCursor(String orgUserName, String token, String endCursor) {
        String query = "{\"query\":\"query { organization(login: \\\"" + orgUserName + "\\\") { repositories(first: 100,after:\\\""+endCursor+"\\\") { edges { repository:node { name } }pageInfo{endCursor, hasNextPage} } } }\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer " + token, query));
        return response.getJSONObject("data").getJSONObject("organization").toString();
    }

    public String getRepositoriesByName(String orgUserName, String token, String repoName) {
        String query = "{\"query\":\"query {search(query: \\\"is:public org:"+orgUserName+" "+repoName+" in:name\\\", type: REPOSITORY, first: 30) {edges {node {... on Repository {name}}}}}\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer " + token, query));
        return response.getJSONObject("data").getJSONObject("search").toString();
    }
}
