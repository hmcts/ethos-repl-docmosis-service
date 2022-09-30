package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Table(name = "case_event", schema = "ccd_data")
@Entity
//@TypeDefs({
//        @TypeDef(
//                typeClass = JsonBinary.class,
//                defaultForType = JsonNode.class
//        ),
//        @TypeDef(
//                typeClass = PostgreSQLEnumType.class,
//                name = "securityclassification"
//        )
//})

@Data
@Builder
@AllArgsConstructor
public class CaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "case_data_id")
    private Long caseDataId;

    @Column(name = "case_type_id")
    private String caseTypeId;

    @Column(name = "state_id")
    private String stateId;

    @JsonInclude()
    @Transient
    @Column(name = "data", columnDefinition = "jsonb")
    private String data;

    @JsonInclude()
    @Transient
    @Column(name = "data_classification", columnDefinition = "jsonb")
    private String dataClassification;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "summary")
    private String summary;

    @Column(name = "description")
    private String description;

    @Column(name = "case_type_version")
    private Long caseTypeVersion;

    @Column(name = "user_first_name")
    private String userFirstName;

    @Column(name = "user_last_name")
    private String userLastName;

    @Column(name = "state_name")
    private String stateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "security_classification")
    private SecurityClassification securityClassification;

    public enum SecurityClassification implements Serializable {
        PUBLIC(1), PRIVATE(2), RESTRICTED(3);

        private final int rank;

        SecurityClassification(int rank) {
            this.rank = rank;
        }
    }
}
