package uk.gov.hmcts.ethos.replacement.docmosis.idam.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ToStringStyle.ourStyle;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails {

    private final String id;
    private final String email;

    private final String forename;
    private final String surname;
    private final List<String> roles;

    public UserDetails(
        String id,
        String email,
        String forename,
        String surname,
        List<String> roles
    ) {
        this.id = id;
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getForename() {
        return forename;
    }

    public Optional<String> getSurname() {
        return Optional.ofNullable(surname);
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ourStyle());
    }
}
