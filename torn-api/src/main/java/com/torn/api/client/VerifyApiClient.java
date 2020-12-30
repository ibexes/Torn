package com.torn.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.torn.api.utils.JsonConverter.convertToJson;
import static com.torn.api.utils.JsonConverter.convertToMember;

public class VerifyApiClient {

    public static Member verify(String key) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/user/?selections=&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToMemberFromJson(convertToJson(response.getBody()));
    }

    static Member convertToMemberFromJson(JsonNode jsonNode) {
        String userId = jsonNode.get("player_id").asText();
        return convertToMember(userId, jsonNode);
    }
}
