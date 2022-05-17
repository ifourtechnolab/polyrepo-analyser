package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class QueryController {

    @Autowired
    QueryService queryService;

    private final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @PostMapping("/storedQueries/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getStoredQueries(@PathVariable int userId){
        try{
            List<Map<String, Object>> response = queryService.getStoredQueries(userId);
            logger.info("List of stored queries : {}",response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IndexOutOfBoundsException e){
            return new ResponseEntity<>(Collections.singletonList(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No Stored Queries")),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to store the save query in database
     * @param title Stored query title
     * @param userId User id
     * @param queryKey Query key to identify the analysis
     * @param repoNamesList List of Repositories selected by user
     * @param orgName GitHub Organization login name
     * @param days Number of days for filter
     * @return ResponseEntity with database operation status
     */
    @PostMapping("/saveQuery")
    public ResponseEntity<Map<String, Object>> saveQuery(@RequestParam String title,@RequestParam int userId,@RequestParam String queryKey, @RequestBody(required = false) RepoNamesList repoNamesList, @RequestParam String orgName, @RequestParam(required = false) Integer days, @RequestParam(required = false) String label){
        try {
            logger.info("Save query of user");
            return new ResponseEntity<>(queryService.saveQueries(new StoredQuery(title,queryKey,userId), repoNamesList, orgName, days, label), HttpStatus.OK);
        }catch (SQLIntegrityConstraintViolationException e ){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query already exists"),HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Process Failed"),HttpStatus.OK);
        }
    }
}
