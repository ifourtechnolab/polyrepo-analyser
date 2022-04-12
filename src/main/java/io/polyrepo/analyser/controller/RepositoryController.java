package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.service.RepositoryService;
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
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    private final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    @GetMapping("/{orgUserName}/repo")
    public ResponseEntity<Map<String,Object>> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositories(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orgUserName}/repo/more")
    public ResponseEntity<Map<String,Object>> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token, @RequestHeader("EndCursor") String endCursor) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositoriesByCursor(orgUserName, token, endCursor), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orgUserName}/repo/{repoName}")
    public ResponseEntity<Map<String,Object>> getRepositoriesByName(@PathVariable String orgUserName, @PathVariable String repoName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositoriesByName(orgUserName, token, repoName), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }

}
