//package uk.gov.hmcts.probate.services.security;
//
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
//import uk.gov.hmcts.reform.auth.checker.core.service.Service;
//import uk.gov.hmcts.reform.auth.checker.core.user.User;
//import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;
//
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//
//@Profile("SECURITY_MOCK")
//@Configuration
//@EnableWebSecurity
//@Order(1)
//public class SecurityMockConfiguration extends WebSecurityConfigurerAdapter {
//
//    private AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter;
//
//    @Autowired
//    public SecurityMockConfiguration(RequestAuthorizer<User> userRequestAuthorizer,
//                                     RequestAuthorizer<Service> serviceRequestAuthorizer,
//                                     AuthenticationManager authenticationManager) {
//        authCheckerServiceAndUserFilter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
//        authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);
//    }
//
//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers(
//                "/cases/**",
//                "/drafts/**",
//                "/submissions/**",
//                "/");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().antMatchers(
//                "/cases/**",
//                "/drafts/**",
//                "/submissions/**"
//        ).permitAll();
//
//    }
//}
