package io.polyrepo.controller;

import io.polyrepo.service.RepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/polyrepo/analyser")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/org/{orgUserName}/repo")
    public Map<String,Object> getRepositories(@PathVariable String orgUserName){
        JSONObject result = new JSONObject(repositoryService.getRepositories(orgUserName));
        return result.toMap();
    }

}
