package io.polyrepo.controller;

import io.polyrepo.service.RepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/polyrepo/analyser")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;


    @GetMapping("/org/{orgUserName}/repo")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token){
        return repositoryService.getRepositories(orgUserName,token);
    }

    @CrossOrigin
    @GetMapping("/org/{orgUserName}/repo/more")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token,@RequestHeader("EndCursor") String endCursor){
        return repositoryService.getRepositoriesByCursor(orgUserName,token,endCursor);
    }

    @CrossOrigin
    @GetMapping("/org/{orgUserName}/repo/{repoName}")
    public ResponseEntity<?> getRepositoriesByName(@PathVariable String orgUserName,@PathVariable String repoName ,@RequestHeader("Authorization") String token){
        return repositoryService.getRepositoriesByName(orgUserName,token,repoName);
    }

}
