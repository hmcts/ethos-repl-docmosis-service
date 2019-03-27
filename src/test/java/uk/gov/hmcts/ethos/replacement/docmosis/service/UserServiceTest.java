package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

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
    public void shouldHaveUserDetails() {
        assertEquals(userService.getUserDetails("TOKEN"), userDetails);
    }

    @Test
    public void shouldCheckAllUserDetails() {
        assertEquals(userDetails, userService.getUserDetails("TOKEN"));
        assertEquals("example@gmail.com", userService.getUserDetails("TOKEN").getEmail());
        assertEquals("Smith", userService.getUserDetails("TOKEN").getForename());
        assertEquals(Collections.singletonList("Worker"), userService.getUserDetails("TOKEN").getRoles());
        assertEquals(Optional.of("John"), userService.getUserDetails("TOKEN").getSurname());
        assertEquals(userDetails.toString(), userService.getUserDetails("TOKEN").toString());
    }
}