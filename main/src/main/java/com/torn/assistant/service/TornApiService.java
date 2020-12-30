package com.torn.assistant.service;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

/**
 * Wrapper client to keep retrying calls if they fail
 */
@Service
public class TornApiService {
    private final RetryTemplate retryTemplate;

    public TornApiService(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

//    public <T> T doWithRetry() {
//        retryTemplate.execute(() -> )
//    }
}
