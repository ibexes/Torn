package com.torn.api.client;

import com.torn.api.model.exceptions.TornApiAccessException;
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
import static com.torn.api.client.FactionApiClient.convertToOrganisedCrimeList;
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
        assertTrue(selectedCrime.getInitiated());
        assertTrue(selectedCrime.getSuccess());
    }
}
