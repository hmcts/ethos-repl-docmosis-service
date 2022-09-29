package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import java.io.Serializable;


    enum SecurityClassification implements Serializable {
        PUBLIC(1), PRIVATE(2), RESTRICTED(3);

        private final int rank;

        SecurityClassification(int rank) {
            this.rank = rank;
        }
    }

