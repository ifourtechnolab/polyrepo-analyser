package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class TrendCapture {
    private int trendId;
    private Date dateOfResult;
    private int result;
    private int queryId;

    public TrendCapture(Date dateOfCapture, int result, int queryId) {
        this.dateOfResult = dateOfCapture;
        this.result = result;
        this.queryId = queryId;
    }
}
