package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "multipleReferenceMidlandsWest")
public class MultipleReferenceMidlandsWest extends MultipleReference {
}
