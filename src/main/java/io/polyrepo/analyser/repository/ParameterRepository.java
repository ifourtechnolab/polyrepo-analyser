package io.polyrepo.analyser.repository;

import io.polyrepo.analyser.model.QueryParameter;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public interface ParameterRepository {
    void saveParameter(QueryParameter queryParameter) throws  SQLException;
}
