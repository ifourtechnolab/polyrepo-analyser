package io.polyrepo.service;

import io.polyrepo.client.OrganizationClient;
import io.polyrepo.constant.ApplicationConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private ApplicationConstants constants;

    public String getOrganization(String name){
        String query = "{\"query\":\"query { search(query : \\\"is:public "+name+" is:name type:org\\\" type : USER first : 15){ edges{ node{ ... on Organization{ name } } } } }\"}";
        JSONObject response = new JSONObject(organizationClient.getOrganization("Bearer "+constants.getToken(), query));
        JSONArray filter = response.getJSONObject("data").getJSONObject("search").getJSONArray("edges");
        return filter.toString();
    }

}
