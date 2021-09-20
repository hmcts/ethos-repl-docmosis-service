package uk.gov.hmcts.ethos.replacement.docmosis.domain.judges_data;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "judge_detail")
public class JudgeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    protected String code;

    protected String name;
    @Column(name="tribunal_office")
    protected String tribunalOffice;
    @Column(name="employment_status")
    protected String employmentStatus;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return employmentStatus;
    }
    public void setStatus(String status) {
        this.employmentStatus = status;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getOffice() {
        return tribunalOffice;
    }
    public void setOffice(String office) {
        this.tribunalOffice = office;
    }
}
