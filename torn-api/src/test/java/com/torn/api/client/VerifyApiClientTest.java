package com.torn.api.client;

import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static com.torn.api.FileReaderUtil.readFileAsString;
import static com.torn.api.utils.JsonConverter.convertToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class VerifyApiClientTest {

    @Test
    public void verifyApiKey() throws IOException, TornApiAccessException {
        String response = readFileAsString(VerifyApiClientTest.class, "player.json");

        Member member = VerifyApiClient.convertToMemberFromJson(convertToJson(response));

        assertEquals(574315, member.getUserId());
        assertEquals(8151, member.getFactionId());
    }
}
