package com.torn.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.torn.api.model.exceptions.IncorrectKeyException;
import com.torn.api.model.exceptions.InvalidAccessException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.AttackLog;
import com.torn.api.model.faction.AttackType;
import com.torn.api.model.faction.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static List<JsonNode> convertJsonToList(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonNode>>() {});
        try {
            return reader.readValue(jsonNode);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static Member convertToMember(String userId, JsonNode memberNode) {
        Member member = new Member();
        member.setUserId(Long.parseLong(userId));
        member.setName(memberNode.get("name").asText());
        member.setLastAction(convertToDate(memberNode.get("last_action").get("timestamp").asLong()));
        try {
            member.setFactionId(memberNode.get("faction").get("faction_id").asLong());
        } catch (NullPointerException ignored) {

        }
        return member;
    }

    public static AttackLog convertToAttackLog(JsonNode jsonNode) {
        AttackLog attackLog = new AttackLog();
        attackLog.setLog(jsonNode.get("code").asText());
        attackLog.setAttackerId(jsonNode.get("attacker_id").asLong());
        attackLog.setAttackerFaction(jsonNode.get("attacker_faction").asLong());
        attackLog.setDefenderId(jsonNode.get("defender_id").asLong());
        attackLog.setDefenderFaction(jsonNode.get("defender_faction").asLong());
        attackLog.setAttackType(AttackType.convertToAttackType(jsonNode.get("result").asText()));
        attackLog.setInitiated(convertToDate(jsonNode.get("timestamp_started").asLong()));
        attackLog.setStealth(jsonNode.get("stealthed").asBoolean());
        attackLog.setAttacker(jsonNode.get("attacker_name").asText());
        attackLog.setDefender(jsonNode.get("defender_name").asText());
        return attackLog;
    }

    public static Date convertToDate(Long epoch) {
        return new Date(epoch * 1000);
    }
}
