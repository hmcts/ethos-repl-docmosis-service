package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private IdamApi idamApi;

    private UserDetails userDetails;

    @Before
    public void setUp() {
        userDetails = HelperTest.getUserDetails();
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
        assertEquals("mail@mail.com", userService.getUserDetails("TOKEN").getEmail());
        assertEquals("Mike", userService.getUserDetails("TOKEN").getFirstName());
        assertEquals("Jordan", userService.getUserDetails("TOKEN").getLastName());
        assertEquals(Collections.singletonList("role"), userService.getUserDetails("TOKEN").getRoles());
        assertEquals(userDetails.toString(), userService.getUserDetails("TOKEN").toString());
    }
}