package io.polyrepo.analyser.service;

import io.polyrepo.analyser.constant.StringConstants;
import io.polyrepo.analyser.model.QueryParameter;
import io.polyrepo.analyser.model.RepoNamesList;
import io.polyrepo.analyser.model.StoredQuery;
import io.polyrepo.analyser.repository.ParameterRepository;
import io.polyrepo.analyser.repository.QueryRepository;
import io.polyrepo.analyser.repository.StoredRepoRepository;
import io.polyrepo.analyser.util.ParameterName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    StoredRepoRepository storedRepoRepository;

    private final Logger logger = LoggerFactory.getLogger(QueryService.class);

    /**
     * This method get all the details of stored queries of current user
     * @param userId Current user id
     * @return Map with the details of stored queries
     * @throws SQLException if error occurs in database operation
     */
    public Map<String, Object> getStoredQueries(int userId) throws SQLException {
        return queryRepository.getStoredQueries(userId);
    }

    /**
     * This method will call repository to save query, parameter and repolist in database
     * @param storedQuery Save query details
     * @param repoNamesList List of Repositories selected by user
     * @param orgName GitHub Organization login name
     * @param days Number of days for filter
     * @return map with status of database operation
     * @throws SQLException if error occurs in database operation
     */
    public Map<String,Object> saveQueries(StoredQuery storedQuery, RepoNamesList repoNamesList, String orgName, Integer days, String label) throws SQLException{
        logger.info("Saving query in database");
        int storedQueryId = queryRepository.saveStoredQuery(storedQuery);
        if(storedQueryId>0){
            logger.info("Saving parameter in database");
            parameterRepository.saveParameter(new QueryParameter(ParameterName.ORGNAME.getParamName(), orgName,storedQueryId));
            if(days!=null){
                parameterRepository.saveParameter(new QueryParameter(ParameterName.DAYS.getParamName(), days.toString(),storedQueryId));
            }
            if(label!=null){
                parameterRepository.saveParameter(new QueryParameter(ParameterName.LABEL.getParamName(), label,storedQueryId));
            }
            if(repoNamesList!=null){
                logger.info("Saving Repolist in database");
                storedRepoRepository.saveRepoNameList(repoNamesList,storedQueryId);
            }
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query saved successfully");
        }
        else{
            return Collections.singletonMap(StringConstants.JSON_MESSAGE_KEY_STRING,"Query not saved");
        }
    }
}
