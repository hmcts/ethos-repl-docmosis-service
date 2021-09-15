package uk.gov.hmcts.ethos.replacement.docmosis.data.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.ethos.replacement.docmosis.data.Venue;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;

import java.util.List;

@Repository
public class VenueRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public VenueRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Venue> find(TribunalOffice tribunalOffice) {
        return jdbcTemplate.query(
                "SELECT * FROM venue WHERE tribunal_office = :tribunalOffice",
                new MapSqlParameterSource()
                        .addValue("tribunal_office", tribunalOffice.name()),
                this.mapper
        );
    }
}
