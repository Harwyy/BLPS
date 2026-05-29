package com.yandex.tracker.jca.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Issue {
    private String id;
    private String key;
    private String summary;
    private String description;
    private Integer queueId;

    public Issue() {}

    public Issue(String summary, String description) {
        this.summary = summary;
        this.description = description;
    }

}