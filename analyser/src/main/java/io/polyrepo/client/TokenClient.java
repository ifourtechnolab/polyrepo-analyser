package io.polyrepo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "token", url = "https://api.github.com/graphql")
public interface TokenClient {

    @PostMapping("")
    String validateToken(@RequestHeader("Authorization") String bearerToken,@RequestBody String query);
}
