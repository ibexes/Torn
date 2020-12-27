package com.torn.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.Contributor;
import com.torn.api.model.faction.Member;
import com.torn.api.model.faction.Stat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FactionApiClient {
    private FactionApiClient() {

    }

    public static List<Member> getMembers(String key) throws JsonProcessingException {
        String url = "https://api.torn.com/faction/?selections=timestamp,basic&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToMemberList(convertToJson(response.getBody()));
    }

    public static Contribution getContribution(String key, Stat stat) throws JsonProcessingException {
        String url = "https://api.torn.com/faction/?selections=timestamp,basic,contributors&stat=" + stat.getValue() + "&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToContribution(stat, convertToJson(response.getBody()));
    }

    static JsonNode convertToJson(String string) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(string);

        if (root.has("error")) {
            throw new RuntimeException("Error in response " + root.get("error"));
        }

        return root;
    }

    static List<Member> convertToMemberList(JsonNode jsonNode) {
        JsonNode members = jsonNode.get("members");
        List<Member> memberList = new ArrayList<>();
        for (Iterator<String> it = members.fieldNames(); it.hasNext(); ) {
            String userId = it.next();
            Member member = convertToMember(userId, members.get(userId));
            memberList.add(member);
        }
        return memberList;
    }

    static Contribution convertToContribution(Stat stat, JsonNode jsonNode) {
        Contribution contribution = new Contribution(stat);

        JsonNode contributors = jsonNode.get("contributors");
        JsonNode members = jsonNode.get("members");
        JsonNode statContributions = contributors.get(stat.getValue());

        for (Iterator<String> it = statContributions.fieldNames(); it.hasNext(); ) {
            String userId = it.next();

            if(statContributions.get(userId).get("in_faction").asInt() == 1) {
                Member member = convertToMember(userId, members.get(userId));
                contribution.addContributor(new Contributor(member, statContributions.get(userId).get("contributed").asLong()));
            }
        }
        return contribution;
    }

    static Member convertToMember(String userId, JsonNode memberNode) {
        Member member = new Member();
        member.setUserId(Long.parseLong(userId));
        member.setName(memberNode.get("name").asText());
        member.setLastAction(new Date(memberNode.get("last_action").get("timestamp").asLong() * 1000));
        return member;
    }
}
