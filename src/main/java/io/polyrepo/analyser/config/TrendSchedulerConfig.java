package io.polyrepo.analyser.config;

import io.polyrepo.analyser.model.*;
import io.polyrepo.analyser.repository.QueryRepository;
import io.polyrepo.analyser.service.QueryService;
import io.polyrepo.analyser.util.ParameterName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class TrendSchedulerConfig {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private QueryService queryService;

    private final Logger logger = LoggerFactory.getLogger(TrendSchedulerConfig.class);

    @Scheduled(cron = "${cron.expression}")
    public void scheduledTrendCapture() {
        try {
            Map<String, Object> listOfAllTrendCapturedQueries = queryRepository.getListOfAllTrendCapturedQueries();
            for (Map.Entry<String, Object> entry :
                    listOfAllTrendCapturedQueries.entrySet()) {
                int result = getResult((StoredQueryList) entry.getValue());
                TrendCapture trendCapture = new TrendCapture();
                trendCapture.setDateOfResult(Date.valueOf(LocalDate.now()));
                trendCapture.setQueryId(Integer.parseInt(entry.getKey()));
                trendCapture.setResult(result);
                queryRepository.saveTrendResult(trendCapture);
            }
        }
        catch (SQLException e){
            logger.debug(e.getMessage());
        }
    }

    private int getResult(StoredQueryList value) {
        List<RepoName> repoNames= new ArrayList<>();
        RepoName repoName = new RepoName();
        for (QueryRepo q:
             value.getQueryRepoList()) {
            repoName.setId(q.getRepoName());
            repoName.setName(q.getRepoName());
            repoNames.add(repoName);
        }
        RepoNamesList repoNamesList = new RepoNamesList();
        repoNamesList.setRepoNames(repoNames);

        String orgName = null;
        int days = 0;
        for (QueryParameter p:
             value.getQueryParameterList()) {
            if(Objects.equals(p.getParamName(), ParameterName.ORGNAME.getParamName())){
                orgName = p.getParamValue();
            }
            if(Objects.equals(p.getParamName(),ParameterName.DAYS.getParamName())){
                days = Integer.parseInt(p.getParamValue());
            }
        }

        Map<String, Object> queryResult = queryService.getQueryResult(value.getBearerToken(), value.getStoredQuery().getQueryId()
                , value.getStoredQuery().getQueryKey(), orgName, days, null, repoNamesList);


        if(Objects.equals(value.getStoredQuery().getQueryKey(), "getPriority1IssuesOpenedBeforeXDaysQuery")){
            return (int) queryResult.get("issueCount");
        }
        else{
            Map<String,Object> search = (Map<String, Object>) queryResult.get("search");
            return (int) search.get("totalPullRequest");
        }
    }
}
