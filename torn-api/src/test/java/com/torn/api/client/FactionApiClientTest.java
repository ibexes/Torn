package com.torn.api.client;

import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Contribution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static com.torn.api.FileReaderUtil.readFileAsString;
import static com.torn.api.model.faction.Stat.SPEED;
import static com.torn.api.utils.JsonConverter.convertToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FactionApiClientTest {

    @Test
    public void convertsContribution() throws IOException, TornApiAccessException {
        String response = readFileAsString(FactionApiClientTest.class, "gymspeed.json");

        Contribution contribution = FactionApiClient.convertToContribution(SPEED, convertToJson(response));

        assertEquals(SPEED, contribution.getStat());
        assertEquals(82, contribution.getContributors().size());
    }
}
