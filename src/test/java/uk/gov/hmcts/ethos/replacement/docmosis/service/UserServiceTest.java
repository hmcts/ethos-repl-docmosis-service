package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private IdamApi idamApi;

    private UserDetails userDetails;

    @Before
    public void setUp() {
        userDetails = new UserDetails("123", "example@gmail.com", "Smith",
                "John", Collections.singletonList("Worker"));
        idamApi = authorisation -> userDetails;
        userService = new UserService(idamApi);
    }

    @Test
    public void getUserDetails() {
        assertEquals(userService.getUserDetails("TOKEN"), userDetails);
    }
}