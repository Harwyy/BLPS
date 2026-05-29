package com.blps.blps.service;

import com.yandex.tracker.jca.api.YandexTrackerConnection;
import com.yandex.tracker.jca.api.YandexTrackerConnectionFactory;
import com.yandex.tracker.jca.model.Issue;
import jakarta.resource.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class YandexTrackerService {

    private final YandexTrackerConnectionFactory connectionFactory;

    @Autowired
    public YandexTrackerService(@Qualifier("yandexTrackerConnectionFactory") YandexTrackerConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Issue createIssue(String summary, String description, Integer queueId) throws ResourceException {
        Issue issue = new Issue(summary, description);
        issue.setQueueId(queueId);

        try (YandexTrackerConnection connection = connectionFactory.getConnection()) {
            return connection.createIssue(issue);
        }
    }

    public Issue updateIssue(String issueKey, String summary, String description) throws ResourceException {
        Issue updates = new Issue();
        updates.setSummary(summary);
        updates.setDescription(description);

        try (YandexTrackerConnection connection = connectionFactory.getConnection()) {
            return connection.updateIssue(issueKey, updates);
        }
    }
}