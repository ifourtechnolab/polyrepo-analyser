package io.polyrepo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "organization", url = "https://api.github.com/graphql")
public interface OrganizationClient {

    @PostMapping("")
    String getOrganization(@RequestHeader("Authorization") String bearerToken,@RequestBody String query);
}
