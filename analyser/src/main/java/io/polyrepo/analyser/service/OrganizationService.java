package io.polyrepo.analyser.service;

import feign.FeignException;
import io.polyrepo.analyser.client.GraphQLClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class OrganizationService {

    @Autowired
    private GraphQLClient client;

    @Value("${getOrganizationListQuery}")
    private String getOrganizationListQuery;

    @Value("${getOrganizationProfileQuery}")
    private String getOrganizationProfileQuery;

    private final Logger LOG = LoggerFactory.getLogger(OrganizationService.class);

    /**
     * This method with fetch and returns the list of organizations that have the same name as mentioned name
     *
     * @param name  GitHub Organization login name
     * @param token GitHub personal access token
     * @return List of organization
     */
    public Map<String, Object> getOrganizationList(String name, String token) throws FeignException, JSONException {
        String query = String.format(getOrganizationListQuery, name);
        ResponseEntity<String> response;
        LOG.info("Getting list of organizations with \"" + name + "\" in name");
        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data").getJSONObject("search");
        return result.toMap();
    }

    /**
     * This method will fetch the name, url and avatar image url of the specified organization
     *
     * @param orgUserName GitHub Organization login name
     * @param token       GitHub personal access token
     * @return Profile details of specified organization
     */
    public Map<String, Object> getOrganizationProfile(String orgUserName, String token) throws FeignException, JSONException {
        String query = String.format(getOrganizationProfileQuery, orgUserName);
        ResponseEntity<String> response;
        LOG.info("Getting Organization profile of : " + orgUserName);
        response = client.getQuery("Bearer " + token, query);
        JSONObject result = new JSONObject(response.getBody()).getJSONObject("data");
        return result.toMap();
    }
}
