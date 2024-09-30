package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private IdamApi idamApi;

    private UserDetails userDetails;

    @Before
    public void setUp() {
        userDetails = HelperTest.getUserDetails();
        idamApi = new IdamApi() {
            @Override
            public UserDetails retrieveUserDetails(String authorisation) {
                return HelperTest.getUserDetails();
            }

            @Override
            public UserDetails getUserByUserId(String authorisation, String userId) {
                return HelperTest.getUserDetails();
            }

            @Override
            public TokenResponse generateOpenIdToken(TokenRequest tokenRequest) {
                return null;
            }
        };
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