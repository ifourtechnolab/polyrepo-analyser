package io.polyrepo.service;

import io.polyrepo.client.GraphQLClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    @Autowired
    private GraphQLClient client;


    public String getOrganization(String name, String token){
        String query = "{\"query\":\"query { search(query : \\\"is:public "+name+" is:name type:org\\\" type : USER first : 15){ edges{ node{ ... on Organization{ name login } } } } }\"}";
        JSONObject response = new JSONObject(client.getQuery("Bearer "+token, query));
//        JSONArray filter = response.getJSONObject("data").getJSONObject("search").getJSONArray("edges");
        JSONObject filter = response.getJSONObject("data").getJSONObject("search");
        return filter.toString();
    }

}
