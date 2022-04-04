package io.polyrepo.analyser.controller;

import feign.FeignException;
import io.polyrepo.analyser.service.OrganizationService;
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
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    private final Logger LOG = LoggerFactory.getLogger(OrganizationController.class);

    @CrossOrigin
    @GetMapping("/{orgName}")
    public ResponseEntity<?> getOrganizationsList(@PathVariable String orgName, @RequestHeader("Authorization") String token) {
        try{
            return new ResponseEntity<>(organizationService.getOrganizationList(orgName,token),HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Unauthorized"), HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest | JSONException e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Bad Request"),HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @GetMapping("/{orgName}/orgProfile")
    public ResponseEntity<?> getOrganizationProfile(@PathVariable String orgName, @RequestHeader("Authorization") String token){
        try{
            return new ResponseEntity<>(organizationService.getOrganizationProfile(orgName,token),HttpStatus.OK);
        }catch (FeignException.Unauthorized e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Unauthorized"),HttpStatus.UNAUTHORIZED);
        }catch (FeignException.BadRequest | JSONException e){
            LOG.error(e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("message","Bad Request"),HttpStatus.BAD_REQUEST);
        }
    }
}
