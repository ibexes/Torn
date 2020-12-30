package com.torn.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torn.api.model.exceptions.IncorrectKeyException;
import com.torn.api.model.exceptions.InvalidAccessException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;

import java.util.Date;

public class JsonConverter {
    public static JsonNode convertToJson(String string) throws JsonProcessingException, TornApiAccessException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(string);

        if (root.has("error")) {
            if (root.get("error").get("code").asInt() == 2) {
                throw new IncorrectKeyException();
            }
            if (root.get("code").get("code").asInt() == 7) {
                throw new InvalidAccessException("'Incorrect ID-entity relation' : A requested selection is private");
            }
            throw new TornApiAccessException("Error in response " + root.get("error"));
        }

        return root;
    }

    public static Member convertToMember(String userId, JsonNode memberNode) {
        Member member = new Member();
        member.setUserId(Long.parseLong(userId));
        member.setName(memberNode.get("name").asText());
        member.setLastAction(new Date(memberNode.get("last_action").get("timestamp").asLong() * 1000));
        try {
            member.setFactionId(memberNode.get("faction").get("faction_id").asLong());
        } catch (NullPointerException ignored) {

        }
        return member;
    }
}
