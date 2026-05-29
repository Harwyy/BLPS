package com.yandex.tracker.jca.api;

import jakarta.resource.ResourceException;

public interface YandexTrackerConnectionFactory {
    YandexTrackerConnection getConnection() throws ResourceException;
}