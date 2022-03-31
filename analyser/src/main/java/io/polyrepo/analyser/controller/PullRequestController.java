package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("org/pr")
public class PullRequestController {

    @Autowired
    private PullRequestService pullRequestService;

    @CrossOrigin
    @PostMapping("{orgUserName}/lastupdate/{days}")
    public ResponseEntity<?> getPullRequestNotUpdatedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody Map<String, List<String>> repoList){
        return pullRequestService.getPRNotUpdatedByDays(token,orgUserName,repoList,days);
    }

}
