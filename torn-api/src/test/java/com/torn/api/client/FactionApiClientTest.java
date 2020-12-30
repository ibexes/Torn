package com.torn.api.client;

import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Contribution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.torn.api.model.faction.Stat.SPEED;
import static com.torn.api.utils.JsonConverter.convertToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FactionApiClientTest {

    @Test
    public void convertsContribution() throws IOException, TornApiAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("gymspeed.json");

        assert resource != null;
        Path fileName = Path.of(resource.getPath());
        String response = Files.readString(fileName);

        Contribution contribution = FactionApiClient.convertToContribution(SPEED, convertToJson(response));

        assertEquals(SPEED, contribution.getStat());
        assertEquals(82, contribution.getContributors().size());
    }
}
