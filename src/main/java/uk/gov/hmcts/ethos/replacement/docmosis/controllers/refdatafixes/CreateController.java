package uk.gov.hmcts.ethos.replacement.docmosis.controllers.refdatafixes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CCDAdminCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CCDAdminRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.CreateService;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityErrorsAdmin;

@RestController
@RequestMapping("/admin/create")
@RequiredArgsConstructor
public class CreateController {
    public static final String ADMIN_CASE_NAME = "Admin";

    private final VerifyTokenService verifyTokenService;
    private final CreateService createService;

    @PostMapping(value = "/aboutToSubmitEvent", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create Admin Case: About to Submit Event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDAdminCallbackResponse> handleAboutToSubmitEvent(
            @RequestHeader("Authorization") String userToken,
            @RequestBody CCDAdminRequest ccdRequest) {
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        var adminData = ccdRequest.getCaseDetails().getCaseData();
        adminData.setName(ADMIN_CASE_NAME);
        List<String> errors = createService.initCreateAdmin(userToken);
        return getCallbackRespEntityErrorsAdmin(errors, ccdRequest.getCaseDetails().getCaseData());
    }
}
