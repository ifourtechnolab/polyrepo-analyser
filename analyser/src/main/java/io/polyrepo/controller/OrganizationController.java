package io.polyrepo.controller;

import io.polyrepo.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/polyrepo/analyser")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/org/{orgName}")
    public String getOrganization(@PathVariable String orgName){
        return organizationService.getOrganization(orgName);
    }
}
