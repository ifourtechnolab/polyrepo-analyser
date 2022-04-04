package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.service.PullRequestService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/org")
public class PullRequestController {

    @Autowired
    private PullRequestService pullRequestService;

    private final Logger LOG = LoggerFactory.getLogger(PullRequestController.class);

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prLastUpdate/{days}")
    public ResponseEntity<?> getPullRequestNotUpdatedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(pullRequestService.getPRNotUpdatedByDays(token, orgUserName, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prUnMerged/{days}")
    public ResponseEntity<?> getPullRequestUnMergedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(pullRequestService.getUnMergedPullRequestByDays(token, orgUserName, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

}
