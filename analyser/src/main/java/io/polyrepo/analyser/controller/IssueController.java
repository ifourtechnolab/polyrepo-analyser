package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.service.IssueService;
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
public class IssueController {

    @Autowired
    private IssueService issueService;

    private final Logger LOG = LoggerFactory.getLogger(IssueController.class);

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/issuesWithPriority1/openSinceBefore/{days}")
    public ResponseEntity<?> getPriority1IssuesOpenedBeforeXDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(issueService.getPriority1IssuesOpenedBeforeXDays(orgUserName, token, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/closedP1IssuesTime")
    public ResponseEntity<?> getClosedP1IssuesTime(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(issueService.getClosedP1IssuesTime(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/closedP2IssuesTime")
    public ResponseEntity<?> getClosedP2IssuesTime(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(issueService.getClosedP2IssuesTime(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }
}
