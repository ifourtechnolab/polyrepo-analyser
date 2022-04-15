package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
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
import java.util.Map;


@RestController
@RequestMapping("/org")
public class IssueController {

    @Autowired
    private IssueService issueService;

    private final Logger logger = LoggerFactory.getLogger(IssueController.class);

    @PostMapping("/{orgUserName}/repo/issuesWithPriority1/openSinceBefore/{days}")
    public ResponseEntity<Map<String,Object>> getPriority1IssuesOpenedBeforeXDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(issueService.getPriority1IssuesOpenedBeforeXDays(orgUserName, token, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orgUserName}/averageResolvingTimeOfP1Issues")
    public ResponseEntity<Map<String,String>> getAverageResolvingTimeOfP1Issues(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(issueService.getAverageResolvingTimeOfP1Issues(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orgUserName}/averageResolvingTimeOfP2Issues")
    public ResponseEntity<Map<String,String>> getAverageResolvingTimeOfP2Issues(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(issueService.getAverageResolvingTimeOfP2Issues(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }
}
