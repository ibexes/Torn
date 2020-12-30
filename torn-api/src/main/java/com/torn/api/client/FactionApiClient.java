package com.torn.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.Contributor;
import com.torn.api.model.faction.Member;
import com.torn.api.model.faction.Stat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.torn.api.utils.JsonConverter.convertToJson;
import static com.torn.api.utils.JsonConverter.convertToMember;

public class FactionApiClient {
    private FactionApiClient() {

    }

    public static List<Member> getMembers(String key) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/?selections=timestamp,basic&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToMemberList(convertToJson(response.getBody()));
    }

    public static Contribution getContribution(String key, Long factionId, Stat stat) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/" + factionId + "?selections=timestamp,basic,contributors&stat=" + stat.getValue() + "&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertToContribution(stat, convertToJson(response.getBody()));
    }

    static List<Member> convertToMemberList(JsonNode jsonNode) {
        JsonNode members = jsonNode.get("members");
        List<Member> memberList = new ArrayList<>();
        for (Iterator<String> it = members.fieldNames(); it.hasNext(); ) {
            String userId = it.next();
            // reverse engineer the member node to include faction details
            JsonNode memberNode = members.get(userId);
            ((ObjectNode) memberNode).putObject("faction").put("faction_id", jsonNode.get("ID").asLong());
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

            if (statContributions.get(userId).get("in_faction").asInt() == 1) {
                JsonNode memberNode = members.get(userId);
                ((ObjectNode) memberNode).putObject("faction").put("faction_id", jsonNode.get("ID").asLong());

                Member member = convertToMember(userId, members.get(userId));
                contribution.addContributor(new Contributor(member, statContributions.get(userId).get("contributed").asLong()));
            }
        }
        return contribution;
    }
}
