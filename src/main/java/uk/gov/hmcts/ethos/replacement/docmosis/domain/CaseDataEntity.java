package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "case_data", schema = "ccd_data.public")
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

    @JsonInclude()
    @Transient
    @Column(name = "data", columnDefinition = "jsonb")
    private String data;

    @JsonInclude()
    @Transient
    @Column(name = "data_classification", columnDefinition = "jsonb")
    private String dataClassification;

    @Column(name = "state")
    private String state;
}
