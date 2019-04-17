package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Document {
    private String url;
    private String name;
    private String type;
    private String description;

}
