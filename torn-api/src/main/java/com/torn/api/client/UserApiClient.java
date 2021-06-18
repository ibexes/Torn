package com.torn.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.torn.api.utils.JsonConverter.convertToJson;
import static com.torn.api.utils.JsonConverter.convertToPlayer;

public class UserApiClient {
    private UserApiClient() {

    }

    public static Player getPlayerDetails(String key, Long id) throws JsonProcessingException, TornApiAccessException {
        String url = "https://api.torn.com/user/"+id+"?selections=profile,personalstats,crimes,timestamp&key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return convertToPlayer(convertToJson(response.getBody()));
    }
}
