package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class TrendCapture {
    private int trendId;
    private Date date;
    private int result;
    private int queryId;

    public TrendCapture(Date date, int result, int queryId) {
        this.date = date;
        this.result = result;
        this.queryId = queryId;
    }
}
