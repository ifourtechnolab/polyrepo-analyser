package io.polyrepo.analyser.util;

import io.polyrepo.analyser.constant.StringConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.*;

public class AverageUtil {
    private static final Logger logger = LoggerFactory.getLogger(AverageUtil.class);

    private AverageUtil(){}
    public static Map<String, String> getAverageTime(ResponseEntity<String> response){
        JSONArray edges = new JSONObject(Objects.requireNonNull(response.getBody())).getJSONObject(StringConstants.JSON_DATA_KEY).getJSONObject(StringConstants.JSON_SEARCH_KEY).getJSONArray("edges");

        List<Long> timeDiffList = new ArrayList<>();
        for(int i = 0; i < edges.length(); i++){
            String createdAt = edges.getJSONObject(i).getJSONObject("node").getString("createdAt");
            String closedAt = edges.getJSONObject(i).getJSONObject("node").getString("closedAt");
            timeDiffList.add(DateUtil.calculateDiffBetweenDates(createdAt,closedAt));
        }

        logger.info("List of time difference : {}",timeDiffList);
        if(timeDiffList.isEmpty()){
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"No average because there are no issues");
        }
        else {
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,DateUtil.calculateAverage(timeDiffList));
        }
    }
}
