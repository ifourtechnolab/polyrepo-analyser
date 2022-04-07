package io.polyrepo.analyser.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "client", url = "https://api.github.com/graphql")
public interface GraphQLClient {

    @PostMapping("")
    ResponseEntity<String> getQuery(@RequestHeader("Authorization") String bearerToken, @RequestBody String query);
}
