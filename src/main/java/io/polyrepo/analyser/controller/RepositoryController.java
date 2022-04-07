package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.service.RepositoryService;
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
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    private final Logger LOG = LoggerFactory.getLogger(RepositoryController.class);

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositories(orgUserName, token), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo/more")
    public ResponseEntity<?> getRepositories(@PathVariable String orgUserName, @RequestHeader("Authorization") String token, @RequestHeader("EndCursor") String endCursor) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositoriesByCursor(orgUserName, token, endCursor), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/{orgUserName}/repo/{repoName}")
    public ResponseEntity<?> getRepositoriesByName(@PathVariable String orgUserName, @PathVariable String repoName, @RequestHeader("Authorization") String token) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositoriesByName(orgUserName, token, repoName), HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message", "Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

}
