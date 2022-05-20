package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.service.QueryService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class QueryController {

    @Autowired
    QueryService queryService;

    private final Logger logger = LoggerFactory.getLogger(QueryController.class);

    /**
     * Endpoint to get all the stored queries of current user
     * @param userId Current user id
     * @return ResponseEntity with the list of stored queries
     */
    @GetMapping("/getQueries/{userId}")
    public ResponseEntity<Map<String, Object>> getStoredQueries(@PathVariable int userId){
        try{
            logger.info("Getting list of stored queries");
            return new ResponseEntity<>(queryService.getStoredQueries(userId), HttpStatus.OK);
        } catch ( SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No Stored Queries"),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to store the save query in database
     * @param title Stored query title
     * @param userId User id
     * @param queryKey Query key to identify the analysis
     * @param repoNamesList List of Repositories selected by user
     * @param orgUserName GitHub Organization login name
     * @param days Number of days for filter
     * @param label Label for filter
     * @return ResponseEntity with database operation status
     */
    @PostMapping("/saveQuery")
    public ResponseEntity<Map<String, Object>> saveQuery(@RequestParam String title,@RequestParam int userId,@RequestParam String queryKey, @RequestBody(required = false) RepoNamesList repoNamesList, @RequestParam String orgUserName, @RequestParam(required = false) Integer days, @RequestParam(required = false) String label){
        try {
            logger.info("Save query of user");
            return new ResponseEntity<>(queryService.saveQueries(new StoredQuery(title,queryKey,userId), repoNamesList, orgUserName, days, label), HttpStatus.OK);
        }catch (SQLIntegrityConstraintViolationException e ){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query already exists"),HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Process Failed"),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to delete particular stored query with its parameter and repo names list
     * @param queryId id of the query to be deleted
     * @return Response entity with the database operation status
     */
    @GetMapping("/deleteQuery/{queryId}")
    public ResponseEntity<Map<String,String>> deleteQuery(@PathVariable int queryId){
        try{
            logger.info("Deleting stored query");
            return new ResponseEntity<>(queryService.deleteQuery(queryId),HttpStatus.OK);
        }catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Process failed"),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to get result of stored query
     * @param token GitHub personal access token
     * @param queryId stored query id
     * @param queryKey Query key to identify the analysis
     * @param orgUserName GitHub Organization login name
     * @param days Number of days for filter
     * @param label Label for filter
     * @param repoNamesList List of Repositories selected by user
     * @return ResponseEntity with the result of stored query
     */
    @PostMapping("/queryResult/{queryId}")
    public ResponseEntity<Map<String,Object>> getQueryResult(@RequestHeader("Authorization") String token,@PathVariable int queryId,@RequestParam String queryKey,@RequestParam String orgUserName,@RequestParam(required = false) Integer days,@RequestParam(required = false) String label,@RequestBody(required = false) RepoNamesList repoNamesList){
        try{
            return new ResponseEntity<>(queryService.getQueryResult(token,queryId,queryKey,orgUserName,days,label,repoNamesList),HttpStatus.OK);
        }catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }
}
