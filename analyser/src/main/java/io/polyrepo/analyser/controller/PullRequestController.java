package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org")
public class PullRequestController {

    @Autowired
    private PullRequestService pullRequestService;

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prLastUpdate/{days}")
    public ResponseEntity<?> getPullRequestNotUpdatedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList){
        return pullRequestService.getPRNotUpdatedByDays(token,orgUserName,repoNamesList,days);
    }

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prUnMerged/{days}")
    public ResponseEntity<?> getPullRequestUnMergedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList){
        return pullRequestService.getUnMergedPullRequestByDays(token,orgUserName,repoNamesList,days);
    }

}
