package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "multipleReferenceLondonSouth")
public class MultipleReferenceLondonSouth extends MultipleReference {
}
