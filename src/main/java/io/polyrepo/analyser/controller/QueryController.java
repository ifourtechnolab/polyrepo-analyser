package io.polyrepo.analyser.controller;

import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
