package io.polyrepo.controller;

import io.polyrepo.service.RepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/polyrepo/analyser")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/org/{orgUserName}/repo")
    public Map<String,Object> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token){
        JSONObject result = new JSONObject(repositoryService.getRepositories(orgUserName,token));
        return result.toMap();
    }

}
