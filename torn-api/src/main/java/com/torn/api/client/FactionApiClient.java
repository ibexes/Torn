package com.torn.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.AttackLog;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.Contributor;
import com.torn.api.model.faction.Member;
import com.torn.api.model.faction.OrganisedCrime;
import com.torn.api.model.faction.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.torn.api.model.faction.OrganisedCrimeType.convertToOrganisedCrimeType;
import static com.torn.api.utils.JsonConverter.convertJsonToList;
import static com.torn.api.utils.JsonConverter.convertToAttackLog;
import static com.torn.api.utils.JsonConverter.convertToDate;
import static com.torn.api.utils.JsonConverter.convertToJson;
import static com.torn.api.utils.JsonConverter.convertToMember;

public class FactionApiClient {
    private static final Logger logger = LoggerFactory.getLogger(FactionApiClient.class);
    private FactionApiClient() {

    }

    public static List<AttackLog> getAttacksFullBetween(String key, String from, String to)
            throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/?selections=basic,attacks&from=" + from + "&to=" + to + "&key=" + key + "&v=" +
                new Random().nextInt(1000000);
        logger.info("Accessing {}", url);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertToAttackLogList(convertToJson(response.getBody()));
    }

    public static List<AttackLog> getAttacksFull(String key) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/?selections=attacks&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertToAttackLogList(convertToJson(response.getBody()));
    }

    public static List<Member> getMembers(String key) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/?selections=timestamp,basic&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToMemberList(convertToJson(response.getBody()));
    }

    public static List<Member> getMembers(String key, Long factionId) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/faction/"+factionId+"?selections=timestamp,basic&key=" + key;
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

    public static List<OrganisedCrime> getOrganisedCrimes(String key) throws TornApiAccessException, JsonProcessingException {
        String url = "https://api.torn.com/faction/?selections=timestamp,crimes&key=" + key;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToOrganisedCrimeList(convertToJson(response.getBody()));
    }

    static List<OrganisedCrime> convertToOrganisedCrimeList(JsonNode jsonNode) throws TornApiAccessException {
        List<OrganisedCrime> organisedCrimes = new ArrayList<>();
        JsonNode crimes = jsonNode.get("crimes");
        if (crimes == null) {
            throw new TornApiAccessException("Unable to get organised crimes");
        }

        for (Iterator<String> it = crimes.fieldNames(); it.hasNext(); ) {
            String crimeId = it.next();
            JsonNode crime = crimes.get(crimeId);

            OrganisedCrime organisedCrime = new OrganisedCrime(Long.parseLong(crimeId));
            organisedCrime.setCrimeType(convertToOrganisedCrimeType(crime.get("crime_id").asInt()));
            organisedCrime.setMoneyGained(crime.get("money_gain").asLong());
            organisedCrime.setRespectGained(crime.get("respect_gain").asLong());
            organisedCrime.setInitiated(crime.get("initiated").asBoolean());
            organisedCrime.setSuccess(crime.get("success").asBoolean());
            organisedCrime.setInitiatedBy(crime.get("initiated_by").asLong());
            organisedCrime.setPlannedBy(crime.get("planned_by").asLong());
            organisedCrime.setPlannedAt(convertToDate(crime.get("time_started").asLong()));
            organisedCrime.setReadyAt(convertToDate(crime.get("time_ready").asLong()));
            organisedCrime.setInitiatedAt(convertToDate(crime.get("time_completed").asLong()));

            List<Long> participantList = new ArrayList<>();
            List<JsonNode> participants = convertJsonToList(crime.get("participants"));
            for (JsonNode participant : participants) {
                for (Iterator<String> pit = participant.fieldNames(); pit.hasNext(); ) {
                    String participantId = pit.next();
                    participantList.add(Long.parseLong(participantId));
                }
            }
            organisedCrime.setParticipants(participantList);
            organisedCrimes.add(organisedCrime);
        }
        return organisedCrimes;
    }


    static  List<AttackLog> convertToAttackLogList(JsonNode jsonNode) {
        JsonNode attacks = jsonNode.get("attacks");
        List<AttackLog> attackLogs = new ArrayList<>();

        for (Iterator<String> it = attacks.fieldNames(); it.hasNext(); ) {
            String id = it.next();
            JsonNode attackNode = attacks.get(id);
            attackLogs.add(convertToAttackLog(attackNode));
        }

        return attackLogs;
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
