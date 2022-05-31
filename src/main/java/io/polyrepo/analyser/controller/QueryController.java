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
            logger.debug("Getting list of stored queries");
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
     * @param orgName GitHub Organization login name
     * @param days Number of days for filter
     * @param label Label for filter
     * @return ResponseEntity with database operation status
     */
    @PostMapping("/saveQuery")
    public ResponseEntity<Map<String, Object>> saveQuery(@RequestParam String title,@RequestParam int userId,@RequestParam String queryKey, @RequestBody(required = false) RepoNamesList repoNamesList, @RequestParam String orgName, @RequestParam(required = false) Integer days, @RequestParam(required = false) String label, @RequestParam String type){
        try {
            logger.debug("Save query of user");
            return new ResponseEntity<>(queryService.saveQueries(new StoredQuery(title,queryKey,userId), repoNamesList, orgName, days, label, type), HttpStatus.OK);
        }catch (SQLIntegrityConstraintViolationException e ){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query already exists"),HttpStatus.OK);
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
        }
    }

    /** Endpoint to store the update query in database
     *
     * @param queryId id of query to be updated
     * @param title Stored query title
     * @param repoNamesList List of Repositories selected by user
     * @param days Number of days for filter
     * @param label Label for filter
     * @return ResponseEntity with database operation status
     */
    @PostMapping("/updateQuery/{queryId}")
    public ResponseEntity<Map<String, Object>> updateQuery(@PathVariable int queryId,@RequestParam String title, @RequestBody(required = false) RepoNamesList repoNamesList, @RequestParam(required = false) Integer days, @RequestParam(required = false) String label){
        try {
            logger.debug("Update query of user");
            return new ResponseEntity<>(queryService.updateQueries(queryId, title, days, label, repoNamesList), HttpStatus.OK);
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
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
            logger.debug("Deleting stored query");
            return new ResponseEntity<>(queryService.deleteQuery(queryId),HttpStatus.OK);
        }catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
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

    /**
     * Endpoint to mark query for trend capture if user has not marked three queries already
     * @param userId Current user id
     * @param queryId id of query to be marked for trend capture
     * @return Response entity with the database operation status
     */
    @PostMapping("{userId}/setTrendCapture/{queryId}")
    public ResponseEntity<Map<String,Object>> setTrendCapture(@PathVariable int userId, @PathVariable int queryId){
        try {
            logger.debug("Set Query For Trend Capture");
            return new ResponseEntity<>(queryService.setTrendCapture(userId,queryId), HttpStatus.OK);
        } catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to un-mark query from trend capture
     * @param queryId id of query to be unmarked from trend capture
     * @return Response entity with the database operation status
     */
    @PostMapping("/unsetTrendCapture/{queryId}")
    public ResponseEntity<Map<String,Object>> unsetTrendCapture(@PathVariable int queryId){
        try {
            logger.debug("Removing Query from Trend Capture");
            return new ResponseEntity<>(queryService.unsetTrendCapture(queryId),HttpStatus.OK);
        } catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to get the list of queries marked for trend capture of current user
     * @param userId Current user id
     * @return ResponseEntity with the list of trend captured queries of current user
     */
    @GetMapping("{userId}/getListOfTrendCapturedQueries")
    public ResponseEntity<Map<String,Object>> getListOfTrendCapturedQueries(@PathVariable int userId) {
        try{
            logger.debug("Getting list of trend captured queries");
            return new ResponseEntity<>(queryService.getListOfTrendCapturedQueries(userId),HttpStatus.OK);
        } catch ( SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No Trend Captured Queries"),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to get the list of results of trend captured in database
     * @param userId Current user id
     * @return ResponseEntity with the list of trend results
     */
    @GetMapping("{userId}/getTrendResults")
    public ResponseEntity<Map<String,Object>> getTrendResults(@PathVariable int userId) {
        try{
            logger.debug("Getting Trend Results");
            return new ResponseEntity<>(queryService.getTrendResults(userId),HttpStatus.OK);
        } catch ( SQLException e) {
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No Trend Result Found"),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to mark query for pin if user has not marked three queries already
     * @param userId Current user id
     * @param queryId id of query to be marked for pin
     * @return Response entity with the database operation status
     */
    @GetMapping("{userId}/setPinned/{queryId}")
    public ResponseEntity<Map<String,Object>> setPinned(@PathVariable int userId, @PathVariable int queryId){
        try {
            logger.debug("Set Query For Trend Capture");
            return new ResponseEntity<>(queryService.setPinned(userId,queryId), HttpStatus.OK);
        } catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to un-mark query from pin
     * @param queryId id of query to be unmarked from pin
     * @return Response entity with the database operation status
     */
    @GetMapping("/unsetPinned/{queryId}")
    public ResponseEntity<Map<String,Object>> unsetPinned(@PathVariable int queryId){
        try {
            logger.debug("Removing Query from Trend Capture");
            return new ResponseEntity<>(queryService.unsetPinned(queryId),HttpStatus.OK);
        } catch (SQLException e){
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_PROCESS_FAILED_VALUE),HttpStatus.OK);
        }
    }

    /**
     * Endpoint to get the list of results of pin with query details from gitHub and database
     * @param userId Current user id
     * @return ResponseEntity with the list of pin result and query details
     */
    @GetMapping("{userId}/getPinnedResults")
    public ResponseEntity<Map<String,Object>> getPinnedResults(@PathVariable int userId) {
        try{
            logger.debug("Getting Pinned Results");
            return new ResponseEntity<>(queryService.getPinnedResults(userId),HttpStatus.OK);
        } catch ( SQLException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No Pinned Result Found"),HttpStatus.OK);
        } catch (FeignException.Unauthorized e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_UNAUTHORIZED_VALUE), HttpStatus.UNAUTHORIZED);
        } catch (FeignException.BadRequest | JSONException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING, StringConstants.JSON_BAD_REQUEST_VALUE), HttpStatus.BAD_REQUEST);
        }
    }
}
