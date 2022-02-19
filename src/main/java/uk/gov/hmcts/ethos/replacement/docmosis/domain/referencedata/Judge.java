package uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "judge")
@Data
public class Judge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String tribunalOffice;
    private String code;
    private String name;
    @Enumerated(EnumType.STRING)
    private JudgeEmploymentStatus employmentStatus;
}
