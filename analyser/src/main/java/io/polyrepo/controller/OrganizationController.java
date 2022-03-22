package io.polyrepo.controller;

import io.polyrepo.service.OrganizationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/polyrepo/analyser")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/org/{orgName}")
    public Map<String,Object> getOrganization(@PathVariable String orgName){
        JSONObject result = new JSONObject(organizationService.getOrganization(orgName));
        return result.toMap();
    }
}
