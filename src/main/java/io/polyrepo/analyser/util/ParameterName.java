package io.polyrepo.analyser.util;

public enum ParameterName {
    ORGNAME("orgName"),
    DAYS("days");

    private final String paramName;

    ParameterName(String paramName){
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
