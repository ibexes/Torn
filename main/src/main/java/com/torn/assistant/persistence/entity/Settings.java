package com.torn.assistant.persistence.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.torn.assistant.utils.CollectionUtils.getRandomElement;

@Entity
public class Settings {
    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    private LocalDateTime start;
    private Integer count;

    @ElementCollection
    private Set<String> apiKey;

    public Settings() {
        this.id = 1L;
        this.apiKey = new HashSet<>();
        this.start = now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getApiKeys() {
        return apiKey;
    }

    public String getApiKey() {
        if (start.plusMinutes(1).isAfter(now())) {
            start = now();
            count = 0;
        } else {
            count ++;
        }
        return getRandomElement(this.apiKey);
    }

    public void setApiKey(Set<String> apiKey) {
        this.apiKey = apiKey;
    }

    public LocalDateTime getStart() {
        if (start == null || start.isBefore(now())) {
            count = 0;
            start = now();
        }
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Integer getCount() {
        if (count == null) {
            count = 0;
            start = now();
        }
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    private static LocalDateTime now() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }
}
