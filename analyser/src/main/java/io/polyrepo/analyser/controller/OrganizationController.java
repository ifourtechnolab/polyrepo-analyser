package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @CrossOrigin
    @GetMapping("/{orgName}")
    public ResponseEntity<?> getOrganizationsList(@PathVariable String orgName, @RequestHeader("Authorization") String token) {
        return organizationService.getOrganizationList(orgName,token);
    }

    @CrossOrigin
    @GetMapping("/{orgName}/orgProfile")
    public ResponseEntity<?> getOrganizationProfile(@PathVariable String orgName, @RequestHeader("Authorization") String token){
        return organizationService.getOrganizationProfile(orgName,token);
    }
}
