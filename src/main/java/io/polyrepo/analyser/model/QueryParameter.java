package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QueryParameter {

    private int paramId;
    private String paramName;
    private String paramValue;
    private int queryId;

    public QueryParameter(String paramName, String paramValue, int queryId) {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.queryId = queryId;
    }
}
