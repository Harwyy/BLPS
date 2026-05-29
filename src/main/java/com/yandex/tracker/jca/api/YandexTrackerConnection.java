package com.yandex.tracker.jca.api;

import com.yandex.tracker.jca.model.Issue;
import jakarta.resource.ResourceException;

public interface YandexTrackerConnection extends AutoCloseable {
    Issue createIssue(Issue issue) throws ResourceException;
    Issue updateIssue(String issueKey, Issue updates);
    void close() throws ResourceException;
}