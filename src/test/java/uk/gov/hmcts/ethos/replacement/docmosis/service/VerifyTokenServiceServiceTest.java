package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
public class VerifyTokenServiceServiceTest {

    @InjectMocks
    private VerifyTokenService verifyTokenService;

    @Before
    public void setUp() {
        verifyTokenService = new VerifyTokenService();
        ReflectionTestUtils.setField(verifyTokenService, "idamJwkUrl", "http://idam-api:5000/jwks");
    }

    @Test
    public void verifyTokenSignature() {
        assertFalse(verifyTokenService.verifyTokenSignature("Bearer "
            + "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92VnYxK3krV2dySDVVaTlXVGlvTHQwPSIsImFsZyI6IlJTMjU2In0"
            + ".eyJzdWIiOiJzc2NzLWNpdGl6ZW40QGhtY3RzLm5ldCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6Ijc1YzEy"
            + "MTk3LWFjYmYtNDg2Zi1iNDI5LTJlYWEwZjMyNWVkMCIsImlzcyI6Imh0dHA6Ly9mci1hbTo4MDgwL29wZW5hbS9vYXV0aDIvaG"
            + "1jdHMiLCJ0b2tlbk5hbWUiOiJhY2Nlc3NfdG9rZW4iLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiYXV0aEdyYW50SWQiOiIwMGZhY"
            + "ThiNy03OWY5LTRiZWQtODI1OS0zZDE0MDEzOGYzZjIiLCJhdWQiOiJzc2NzIiwibmJmIjoxNTc4NTAwNDU0LCJncmFudF90eXBlI"
            + "joiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIl0sImF1dGhfdGltZSI6MTU3O"
            + "DUwMDQ1MTAwMCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1Nzg1MjkyNTQsImlhdCI6MTU3ODUwMDQ1NCwiZXhwaXJlc19pbiI6M"
            + "jg4MDAsImp0aSI6ImNkMTgxODM3LTdlMmUtNDY1Ny05ZTgwLTk4NWE3ZjVmZDMzYiJ9.SZOd981fC1bdMWehXKsUl0B9vEXRr7-"
            + "NBKl6IaFIoS573rNjKgcIzChMaxcmc-anOxJqgF8Lan7RdMCIb4Y-zGG3TzfGAG7elpmXJVsogPKCWJlGF"
            + "CJm_wU-h_cqAcL2llgqnNkkms43lgvyfIdiXv3J-00qBHzMy3jG5mLOE5YZet1LKf3IiRNZxI5Vx6L2Afdox1jiKGQGGt2bNx7-"
            + "rcYS8VVVZI-ovo7lbbWU6Mi5lWI19q2AS9jGcK5U4hcIU06JzoWGsh-Ob1xkq7VtJKyrOSiUth-SjY5PqQzjvpuEO8MrLWTI0sCaWR"
            + "HbmbF0bHICGO17bQ42_PfTHgza4A"
        ));
    }

}