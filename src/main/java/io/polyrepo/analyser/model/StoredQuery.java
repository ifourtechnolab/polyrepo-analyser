package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoredQuery {
    private int id;
    private String title;
    private String queryKey;
    private int userId;

    public StoredQuery(String title, String queryKey, int userId) {
        this.title = title;
        this.queryKey = queryKey;
        this.userId = userId;
    }
}
