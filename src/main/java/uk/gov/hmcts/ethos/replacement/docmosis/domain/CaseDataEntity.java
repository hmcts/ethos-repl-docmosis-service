package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import com.fasterxml.jackson.databind.JsonNode;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@Table(name = "case_data")
@Entity
public class CaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "jurisdiction")
    private String jurisdiction;

    @Column(name = "reference")
    private Long reference;

    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

    @Column(name = "data_classification", columnDefinition = "jsonb")
    private JsonNode dataClassification;

    @Column(name = "state")
    private String state;
}
