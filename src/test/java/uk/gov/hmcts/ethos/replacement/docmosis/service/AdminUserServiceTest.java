package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminUserServiceTest {

    @InjectMocks
    private AdminUserService adminUserService;
    @Mock
    private AccessTokenService accessTokenService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(adminUserService, "caseWorkerUserName", "example@gmail.com");
        ReflectionTestUtils.setField(adminUserService, "caseWorkerPassword", "123456");
    }

    @Test
    public void shouldGetAdminUserToken() {
        when(accessTokenService.getAccessToken(anyString(), anyString())).thenReturn("TOKEN");
        assertEquals("TOKEN", adminUserService.getAdminUserToken());
        verify(accessTokenService).getAccessToken("example@gmail.com", "123456");
    }

    @Test
    public void shouldClearAdminUserTokenCacheWithoutCallingIdam() {
        adminUserService.emptyAdminUserToken();
        verifyNoInteractions(accessTokenService);
    }
}
