package io.polyrepo.analyser.service;

import io.polyrepo.analyser.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    QueryRepository queryRepository;


    public List<Map<String, Object>> getStoredQueries(int userId) throws IndexOutOfBoundsException{
        return queryRepository.getStoredQueries(userId);
    }
}
