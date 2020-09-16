package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

@Slf4j
@Service("multipleHelperService")
public class MultipleHelperService {

    private final SingleCasesReadingService singleCasesReadingService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @Autowired
    public MultipleHelperService(SingleCasesReadingService singleCasesReadingService) {
        this.singleCasesReadingService = singleCasesReadingService;
    }

    public void addLeadMarkUp(String userToken, String caseTypeId, MultipleData multipleData) {

        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(
                userToken,
                caseTypeId,
                multipleData.getLeadCase());

        if (submitEvent != null) {

            multipleData.setLeadCase(MultiplesHelper.generateLeadMarkUp(ccdGatewayBaseUrl, String.valueOf(submitEvent.getCaseId())));

        } else {

            log.info("No lead case found for: " + multipleData.getLeadCase());

        }

    }

}
