package com.torn.assistant.service;

import com.torn.assistant.model.dto.UserDTO;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.dao.OrganisedCrimeDao;
import com.torn.assistant.persistence.entity.OrganisedCrime;
import com.torn.assistant.persistence.entity.User;
import com.torn.assistant.persistence.service.FactionService;
import com.torn.assistant.persistence.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FactionOrganisedCrimeServiceTest {
    @Mock
    private OrganisedCrimeDao organisedCrimeDao;
    @Mock
    private FactionDao factionDao;
    @Mock
    private UserService userService;
    @Mock
    private FactionService factionService;

    @InjectMocks
    private FactionOrganisedCrimeService sut;

    // test is incomplete
    @Test
    public void getRankings() {
        // create some PAs...
        List<User> pa1 = Arrays.asList(user(1L), user(2L), user(3L), user(5L));
        List<User> pa2 = Arrays.asList(user(2L), user(3L), user(4L), user(5L));

        OrganisedCrime ocPa1 = new OrganisedCrime();
        ocPa1.setParticipants(pa1);
        OrganisedCrime ocPa2 = new OrganisedCrime();
        ocPa2.setParticipants(pa2);


        List<OrganisedCrime> organisedCrimes = Arrays.asList(ocPa1, ocPa2);
        doReturn(organisedCrimes).when(organisedCrimeDao).findByFactionOrderByCrimeTypeDesc(any());

        List<UserDTO> rankings = sut.getPredictedRankings("");
        assertEquals(5, rankings.size());
        assertEquals(1L, rankings.get(0).getUserId());
        assertEquals(2L, rankings.get(1).getUserId());
        assertEquals(3L, rankings.get(2).getUserId());
//        assertEquals(4L, rankings.get(3).getUserId());
//        assertEquals(5L, rankings.get(4).getUserId());
    }

    private User user(Long id) {
        return new User(id, "");
    }
}
