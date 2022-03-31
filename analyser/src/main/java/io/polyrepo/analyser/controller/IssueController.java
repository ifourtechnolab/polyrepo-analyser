package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/org")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/issuesWithPriority1")
    public ResponseEntity<?> getIssueWithPriority1(@PathVariable String orgUserName, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        return issueService.getIssueWithPriority1(orgUserName, token, repoNamesList);
    }

}