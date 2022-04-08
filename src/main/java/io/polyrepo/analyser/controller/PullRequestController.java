package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
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
import java.util.Map;

@RestController
@RequestMapping("/org")
public class PullRequestController {

    @Autowired
    private PullRequestService pullRequestService;

    private final Logger logger = LoggerFactory.getLogger(PullRequestController.class);

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prLastUpdate/{days}")
    public ResponseEntity<Map<String,Object>> getPullRequestNotUpdatedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(pullRequestService.getPRNotUpdatedByDays(token, orgUserName, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/{orgUserName}/repo/prUnMerged/{days}")
    public ResponseEntity<Map<String,Object>> getPullRequestUnMergedByDays(@PathVariable String orgUserName, @PathVariable int days, @RequestHeader("Authorization") String token, @RequestBody RepoNamesList repoNamesList) {
        try {
            return new ResponseEntity<>(pullRequestService.getUnMergedPullRequestByDays(token, orgUserName, repoNamesList, days), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

}
