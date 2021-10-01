package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.items.TestDynamicListTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.TestDynamicListType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityNoErrors;

@RestController
@Slf4j
public class TestDynamicListController {

    private static final String INVALID_TOKEN = "Invalid Token {}";

    private final VerifyTokenService verifyTokenService;

    public TestDynamicListController(VerifyTokenService verifyTokenService) {
        this.verifyTokenService = verifyTokenService;
    }

    @PostMapping(value = "/populateTestDynamicList", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "create a case for a caseWorker.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Accessed successfully",
                response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CCDCallbackResponse> populateDynamicList(
            @RequestBody CCDRequest ccdRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("TestDynamicListController");

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error(INVALID_TOKEN, userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        if (CollectionUtils.isEmpty(ccdRequest.getCaseDetails().getCaseData().getTestDynamicListCollection())) {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(List.of(
                    new DynamicValueType("code1", "Item 1"),
                    new DynamicValueType("code2", "Item 2"),
                    new DynamicValueType("code3", "Item 3")
            ));

            var testDynamicListType = new TestDynamicListType();
            testDynamicListType.setTestDynamicList(dynamicFixedListType);
            var testDynamicListTypeItem = new TestDynamicListTypeItem();
            testDynamicListTypeItem.setValue(testDynamicListType);

            var collection = List.of(testDynamicListTypeItem);
            ccdRequest.getCaseDetails().getCaseData().setTestDynamicListCollection(collection);
        }

        return getCallbackRespEntityNoErrors(ccdRequest.getCaseDetails().getCaseData());
    }
}
