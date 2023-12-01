package uk.gov.hmcts.ethos.replacement.docmosis.controllers.refdatafixes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ecm.common.model.ccd.CCDCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.CCDAdminCallbackResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.CCDAdminRequest;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityErrorsAdmin;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.CallbackRespHelper.getCallbackRespEntityNoErrors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReferenceDataFixesController {

    private static final String LOG_MESSAGE = "received notification request for case reference :    ";

    private final VerifyTokenService verifyTokenService;
    private final ReferenceDataFixesService referenceDataFixesService;

    /**
     * This service Gets userToken as a parameter for security validation
     * and ccdRequest data which has adminData as an object.
     * Initializes AdminData to null values to not show any existing values for
     * judge code updates.
     *
     * @param  userToken        Used for authorization
     *
     * @param ccdRequest        AdminData which is a generic data type for most of the
     *                          methods which holds judge code, dates
     *                          and tribunal office.
     * @return ResponseEntity   It is an HTTPEntity response which has CCDCallbackResponse that
     *                          includes adminData
     */
    @PostMapping(value = "/initAdminData", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Initialise judge codes data to null values")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDAdminCallbackResponse> initAdminData(
            @RequestHeader("Authorization") String userToken,
            @RequestBody CCDAdminRequest ccdRequest) {
        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }

        AdminData adminData = ccdRequest.getCaseDetails().getCaseData();
        referenceDataFixesService.initAdminData(adminData);

        return getCallbackRespEntityNoErrors(adminData);
    }

    @PostMapping(value = "/updateJudgesItcoReferences", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "update the judges' ITCO references")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDAdminCallbackResponse> updateJudgesItcoReferences(
            @RequestBody CCDAdminRequest ccdAdminRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("UPDATE JUDGES ITCO REFERENCES ---> " + LOG_MESSAGE + ccdAdminRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }
        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(userToken);
        AdminData caseData = referenceDataFixesService.updateJudgesItcoReferences(
                ccdAdminRequest.getCaseDetails(), userToken, dataSource);

        return getCallbackRespEntityNoErrors(caseData);
    }

    @PostMapping(value = "/insertClaimServedDate", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert the claim served date for existing cases")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accessed successfully",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CCDCallbackResponse.class))
            }),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CCDAdminCallbackResponse> insertClaimServedDate(
            @RequestBody CCDAdminRequest ccdAdminRequest,
            @RequestHeader(value = "Authorization") String userToken) {
        log.info("INSERT CLAIM SERVED DATE ---> " + LOG_MESSAGE + ccdAdminRequest.getCaseDetails().getCaseId());

        if (!verifyTokenService.verifyTokenSignature(userToken)) {
            log.error("Invalid Token {}", userToken);
            return ResponseEntity.status(FORBIDDEN.value()).build();
        }
        List<String> errors = new ArrayList<>();
        RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(userToken);
        AdminData caseData = referenceDataFixesService.insertClaimServedDate(
            ccdAdminRequest.getCaseDetails(), userToken, dataSource, errors);

        return getCallbackRespEntityErrorsAdmin(errors, caseData);
    }

}
