package io.polyrepo.analyser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class StoredQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "q_id")
    private int queryId;

    @Column(name = "title")
    private String title;

    @Column(name = "query")
    private String queryKey;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "is_trend_captured")
    private boolean isTrendCaptured;

    @Column(name = "is_pinned")
    private boolean isPinned;

    public StoredQuery(String title, String queryKey, int userId) {
        this.title = title;
        this.queryKey = queryKey;
        this.userId = userId;
    }
}
