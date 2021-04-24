package com.torn.assistant.service;

import de.raik.webhook.Webhook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebhookService {
    private final Map<String, List<Webhook>> webhookMap = new HashMap<>();

    public void enqueue(String url, Webhook webhook) {
        synchronized (webhookMap) {
            if (!webhookMap.containsKey(url)) {
                webhookMap.put(url, new ArrayList<>());
            }
            webhookMap.get(url).add(webhook);
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void send() {
        synchronized (webhookMap) {
            for(String url : webhookMap.keySet()) {
                List<Webhook> webhooks = webhookMap.get(url);
                if(!webhooks.isEmpty()) {
                    try {
                        webhooks.remove(0).execute();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
