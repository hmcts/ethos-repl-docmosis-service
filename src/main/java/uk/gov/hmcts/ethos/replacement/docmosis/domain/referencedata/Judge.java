package uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
