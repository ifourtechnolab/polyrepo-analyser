package io.polyrepo.controller;

import io.polyrepo.service.OrganizationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/polyrepo/analyser")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @CrossOrigin
    @GetMapping("/org/{orgName}")
    public ResponseEntity<?> getOrganization(@PathVariable String orgName, @RequestHeader("Authorization") String token) {
        return organizationService.getOrganization(orgName,token);
    }

    @CrossOrigin
    @GetMapping("org/{orgName}/orgProfile")
    public ResponseEntity<?> getOrganizationProfile(@PathVariable String orgName, @RequestHeader("Authorization") String token){
        return organizationService.getOrganizationProfile(orgName,token);
    }
}
