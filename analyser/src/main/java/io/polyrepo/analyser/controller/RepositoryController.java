package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token){
        return repositoryService.getRepositories(orgUserName,token);
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo/more")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token,@RequestHeader("EndCursor") String endCursor){
        return repositoryService.getRepositoriesByCursor(orgUserName,token,endCursor);
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo/{repoName}")
    public ResponseEntity<?> getRepositoriesByName(@PathVariable String orgUserName,@PathVariable String repoName ,@RequestHeader("Authorization") String token){
        return repositoryService.getRepositoriesByName(orgUserName,token,repoName);
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo/{repoName}/defaultbranch")
    public ResponseEntity<?> getDefaultBranch(@PathVariable String orgUserName,@PathVariable String repoName ,@RequestHeader("Authorization") String token){
        return repositoryService.getDefaultBranchOfRepo(orgUserName,token,repoName);
    }

}
