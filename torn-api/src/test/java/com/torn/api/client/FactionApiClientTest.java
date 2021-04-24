package com.torn.api.client;

import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.AttackLog;
import com.torn.api.model.faction.Contribution;
import com.torn.api.model.faction.OrganisedCrime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.torn.api.FileReaderUtil.readFileAsString;
import static com.torn.api.client.FactionApiClient.convertToAttackLogList;
import static com.torn.api.client.FactionApiClient.convertToOrganisedCrimeList;
import static com.torn.api.model.faction.AttackType.LOST;
import static com.torn.api.model.faction.AttackType.MUGGED;
import static com.torn.api.model.faction.OrganisedCrimeType.POLITICAL_ASSASSINATION;
import static com.torn.api.model.faction.Stat.SPEED;
import static com.torn.api.utils.JsonConverter.convertToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FactionApiClientTest {

    @Test
    public void convertsContribution() throws IOException, TornApiAccessException {
        String response = readFileAsString(FactionApiClientTest.class, "gymspeed.json");

        Contribution contribution = FactionApiClient.convertToContribution(SPEED, convertToJson(response));

        assertEquals(SPEED, contribution.getStat());
        assertEquals(82, contribution.getContributors().size());
    }

    @Test
    public void convertsToOrganisedCrimeList() throws TornApiAccessException, IOException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String response = readFileAsString(FactionApiClientTest.class, "organisedcrimes.json");
        List<OrganisedCrime> organisedCrimes = convertToOrganisedCrimeList(convertToJson(response));

        assertEquals(250, organisedCrimes.size());
        List<OrganisedCrime> selectedCrimeList = organisedCrimes.stream()
                .filter(organisedCrime -> organisedCrime.getId() == 8276664).collect(Collectors.toList());
        assertEquals(1, selectedCrimeList.size());
        OrganisedCrime selectedCrime = selectedCrimeList.get(0);

        assertEquals(150000000L, selectedCrime.getMoneyGained());
        assertEquals(204L, selectedCrime.getRespectGained());
        assertEquals(2525435L, selectedCrime.getInitiatedBy());
        assertEquals(2015200L, selectedCrime.getPlannedBy());
        assertEquals(simpleDateFormat.parse("2020-11-05T19:26:18"), selectedCrime.getPlannedAt());
        assertEquals(simpleDateFormat.parse("2020-11-13T22:23:01"), selectedCrime.getInitiatedAt());
        assertEquals(simpleDateFormat.parse("2020-11-13T19:26:18"), selectedCrime.getReadyAt());
        assertEquals(POLITICAL_ASSASSINATION, selectedCrime.getCrimeType());
        assertTrue(selectedCrime.getParticipants().contains(1280063L));
        assertTrue(selectedCrime.getParticipants().contains(2015200L));
        assertTrue(selectedCrime.getParticipants().contains(79148L));
        assertTrue(selectedCrime.getParticipants().contains(248367L));
        assertEquals(79148L, selectedCrime.getParticipants().get(2));
        assertTrue(selectedCrime.getInitiated());
        assertTrue(selectedCrime.getSuccess());
    }

    @Test
    public void convertsToAttackLogsList() throws IOException, TornApiAccessException {
        String response = readFileAsString(FactionApiClientTest.class, "factionattacks.json");

        List<AttackLog> attackLogs = convertToAttackLogList(convertToJson(response));
        assertEquals(100, attackLogs.size());

        List<AttackLog> selectedAttackLogList = attackLogs.stream()
                .filter(attackLog -> attackLog.getLog().equals("7e6e2d90a95b6d5fb7098e8732bf9bdc")).collect(Collectors.toList());

        assertEquals(1, selectedAttackLogList.size());
        assertEquals(2495972, selectedAttackLogList.get(0).getAttackerId());
        assertEquals(4, selectedAttackLogList.get(0).getDefenderId());
        assertEquals(0, selectedAttackLogList.get(0).getDefenderFaction());
        assertEquals(LOST, selectedAttackLogList.get(0).getAttackType());

    }

    @Test
    public void convertsToAttackLogsListStealthed() throws IOException, TornApiAccessException {
        String response = readFileAsString(FactionApiClientTest.class, "factionattacks.json");

        List<AttackLog> attackLogs = convertToAttackLogList(convertToJson(response));
        assertEquals(100, attackLogs.size());

        List<AttackLog> selectedAttackLogList = attackLogs.stream()
                .filter(attackLog -> attackLog.getLog().equals("39ef947b2f8ae3dc3fd16b4f7bda1df5")).collect(Collectors.toList());

        assertEquals(1, selectedAttackLogList.size());
        assertEquals(0, selectedAttackLogList.get(0).getAttackerId());
        assertEquals(914502, selectedAttackLogList.get(0).getDefenderId());
        assertEquals(8151, selectedAttackLogList.get(0).getDefenderFaction());
        assertEquals(MUGGED, selectedAttackLogList.get(0).getAttackType());
        assertTrue(selectedAttackLogList.get(0).getStealth());
    }
}
