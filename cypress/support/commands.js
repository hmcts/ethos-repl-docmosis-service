// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
import 'cypress-file-upload';

const date = new Date();

Cypress.Commands.add('aatLogin', () => {
    cy.visit('https://manage-case.aat.platform.hmcts.net')
    cy.get('#username').type('eric.ccdcooper@gmail.com').should('have.value', 'eric.ccdcooper@gmail.com');
    cy.get('#password').type('Nagoya0102').should('have.value', 'Nagoya0102');
    cy.wait(1000)
    cy.get('.button').click()
    cy.wait(5000);
})

Cypress.Commands.add('rejectCase' , () => {
    cy.get('#next-step').select('Accept/Reject Case');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#preAcceptCase_caseAccepted_No').click();
    cy.wait(1000)
    cy.get('#dateRejected-day').type(date.getDate().toString());
    cy.get('#dateRejected-month').type((date.getMonth()+1).toString());
    cy.get('#dateRejected-year').type(date.getFullYear().toString());
    cy.get('fieldset > :nth-child(2) > .form-label').click();
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('acceptCase', () => {
    cy.get('#next-step').select('Accept/Reject Case');
    cy.get('.button:nth-child(2)').click();
    cy.get('#preAcceptCase_caseAccepted_radio > :nth-child(1) > .form-label', {timeout: 10000}).should('be.visible').click();
    cy.get('#dateAccepted-day').type(date.getDate().toString());
    cy.get('#dateAccepted-month').type((date.getMonth()+1).toString());
    cy.get('#dateAccepted-year').type(date.getFullYear().toString());
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    // Submit page
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
    cy.wait(1000)
    cy.get('#caseStateDesc > dt > ccd-markdown > div > .markdown > h4').contains('Case Status: Accepted');

})

Cypress.Commands.add('addClaimantRepresentative', () => {
    cy.get('#next-step').select('Claimant Representative');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#claimantRepresentedQuestion_Yes').click();
    cy.get('#representativeClaimantType_name_of_representative').type('Claimant Rep');
    cy.get('#representativeClaimantType_name_of_organisation').type('Claimant Org');
    cy.get('#representativeClaimantType_representative_occupation').select('Solicitor');
    cy.get('#representativeClaimantType_representative_address_representative_address_postcodeInput').type('B44 8AS');
    cy.get('.button-30').click();
    cy.wait(1000)
    cy.get('#representativeClaimantType_representative_address_representative_address_addressList').select('16: Object');
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('addRespondentRepresentative', () => {
    cy.get('#next-step').select('Respondent Representative');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.panel > .button').click();
    cy.get('#repCollection_0_resp_rep_name').type('Test Resp').should('have.value', 'Test Resp');
    cy.get('#repCollection_0_name_of_representative').type('Respondent Representative One').should('have.value', 'Respondent Representative One');
    cy.get('#repCollection_0_representative_occupation').select('Union');
    cy.get('#repCollection_0_representative_address_representative_address_postcodeInput').type('B44 8AS').should('have.value', 'B44 8AS')
    cy.get('.button-30').click();
    cy.wait(1000)
    cy.get('#repCollection_0_representative_address_representative_address_addressList').select('5: Object');
    cy.get('.button:nth-child(3)').click();
    cy.wait(1000)
    cy.get('.form').submit();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('addJurisdictions', () => {
    cy.get('#next-step').select('Jurisdiction');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.panel > .button').click()
    cy.get('#jurCodesCollection_0_juridictionCodesList').select('ADG');
    cy.get('#jurCodesCollection_0_judgmentOutcome').select('Not allocated');
    cy.get('.button:nth-child(4)').click();
    cy.get('#jurCodesCollection_1_juridictionCodesList').select('ADT');
    cy.get('#jurCodesCollection_1_judgmentOutcome').select('Not allocated');
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.form').submit();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('listHearing', () => {
    cy.get('#next-step').select('List Hearing');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.panel > .button').click();
    cy.get('#hearingCollection_0_hearingNumber').type('1');
    cy.get('#hearingCollection_0_Hearing_type').select('Preliminary Hearing');
    cy.get('#hearingCollection_0_hearingPublicPrivate').select('Public');
    cy.get('#hearingCollection_0_hearingFormat-Hybrid').click();
    cy.get('#hearingCollection_0_Hearing_venue').select('Barnstaple'); // needs reworking due to other offices
    cy.get('#hearingCollection_0_hearingEstLengthNum').type('1');
    cy.get('#hearingCollection_0_hearingEstLengthNumType').select('Days');
    cy.get('#hearingCollection_0_hearingSitAlone > fieldset > :nth-child(3) > .form-label').click();
    cy.get('#hearingCollection_0_hearingDateCollection .button').click();
    cy.get('#listedDate-day').type((date.getDate() + 1).toString());
    cy.get('#listedDate-month').type((date.getMonth() + 1).toString());
    cy.get('#listedDate-year').type((date.getFullYear()).toString());
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.form').submit();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('allocateHearing', () => {
    cy.get('#next-step').select('Allocate Hearing');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#hearingCollection_0_judge').select('A Judge');
    cy.get('#hearingCollection_0_hearingERMember').select('ER Member');
    cy.get('#hearingCollection_0_hearingEEMember').select('EE Member');
    cy.get('#hearingCollection_0_hearingDateCollection_0_hearingRoomBarnstaple').select('* Not Allocated');
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_clerk').select('A Clerk');
    cy.wait(2000)
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(2000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('hearingDetails', () => {
    cy.get('#next-step').select('Hearing Details');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_status').select('Heard')
    cy.get('#hearingCollection_0_hearingDateCollection_0_hearingCaseDisposed_Yes').click();
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_part_heard_radio > .multiple-choice:nth-child(1) > .form-label').click();
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_part_heard_No').click();
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_reserved_judgement_radio > .multiple-choice:nth-child(1) > .form-label').click();
    cy.get('#hearingCollection_0_hearingDateCollection_0_Hearing_reserved_judgement_No').click();
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('closeCase', () => {
    cy.get('#next-step').select('Close Case');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#clerkResponsible').select('A Clerk');
    cy.get('#fileLocation').select('DORMANT');
    cy.get('#caseNotes').type('Case Closed');
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
    cy.get('#caseStateDesc > dt > ccd-markdown > div > .markdown > h4').contains('Case Status: Closed');
})

Cypress.Commands.add('restrictedReporting', () => {
    cy.get('#next-step').select('Restricted Reporting');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#restrictedReporting_requestedBy').select('Respondent');
    cy.get('#restrictedReporting_imposed_Yes').click();
    cy.get('#dateCeased-day').type('20');
    cy.get('#dateCeased-month').type('07');
    cy.get('#dateCeased-year').type('2021');
    cy.get('#restrictedReporting_rule503b_Yes').click();
    cy.get('#startDate-day').type((date.getDate()).toString());
    cy.get('#startDate-month').type((date.getMonth() + 1).toString());
    cy.get('#startDate-year').type((date.getFullYear()).toString());
    cy.get('#restrictedReporting_excludedRegister').select('1: No');
    cy.get('#restrictedReporting_deletedPhyRegister_No').click();
    cy.get('#restrictedReporting_excludedNames').type('Test Name');
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit()
})

Cypress.Commands.add('depositOrder', () => {
    cy.get('#next-step').select('Deposit Order');
    cy.get('.button:nth-child(2)').click();
    cy.wait(2000)
    cy.get('.panel > .button').click();
    cy.get('#depositType_0_Deposit_amount').type('1000');
    cy.get('#depositType_0_depositOrderAgainst').select('Claimant');
    cy.get('#depositType_0_deposit_requested_by').select('Respondent');
    cy.get('#depositType_0_deposit_covers').select('All');
    cy.get('#depositType_0_deposit_time_ext_No').click();
    cy.get('#depositType_0_depositReceived_No').click();
    cy.get('#depositType_0_deposit_refund_No').click();
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('bfAction', () => {
    cy.get('#next-step').select('B/F Action');
    cy.get('.button:nth-child(2)').click();
    cy.get('.panel > .button').click();
    cy.get('#bfActions_0_cwActions').select('Other action');
    cy.get('#bfDate-day').type((date.getDate()).toString());
    cy.get('#bfDate-month').type((date.getMonth() + 1).toString());
    cy.get('#bfDate-year').type((date.getFullYear()).toString());
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('judgements', () => {
    cy.get('#next-step').select('Judgment');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.panel > .button').click();
    cy.get('#judgementCollection_0_non_hearing_judgment_Yes').click();
    cy.get('#judgementCollection_0_judgement_type').select('Case Management');
    cy.get('#judgementCollection_0_liability_optional').select('Liability');
    cy.get('#date_judgment_made-day').type(date.getDate().toString());
    cy.get('#date_judgment_made-month').type((date.getMonth() + 1).toString());
    cy.get('#date_judgment_made-year').type(date.getFullYear().toString());
    cy.get('#date_judgment_sent-day').type(date.getDate().toString());
    cy.get('#date_judgment_sent-month').type((date.getMonth() + 1).toString());
    cy.get('#date_judgment_sent-year').type(date.getFullYear().toString());
    cy.get('#judgementCollection_0_judgement_details_reasons_given_No').click();
    cy.get('#judgementCollection_0_judgement_details_awardMade_No').click();
    cy.get('#judgementCollection_0_judgement_details_non-financial_award_No').click();
    cy.get('#judgementCollection_0_judgement_details_certificateOfCorrection_No').click();
    cy.get('#judgementCollection_0_Judgement_costs_costs_question_No').click();
    cy.get('#judgementCollection_0_reconsiderations_reconsideration_No').click();
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('reinstateCase', () => {
    cy.get('#next-step').select('Reinstate Case');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#positionType').select('Case input in error');
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})

Cypress.Commands.add('caseTransfer', () => {
    cy.get('#next-step').select('Case Transfer');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('#officeCT').select('Manchester');
    cy.get('#positionTypeCT').select('Case transferred - same country');
    cy.get('#reasonForCT').type('Test Transfer');
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
    cy.get('#caseStateDesc > dt > ccd-markdown > div > .markdown > h4').contains('Case Status: Transferred');
})

Cypress.Commands.add('uploadDocument', () => {
    cy.get('#next-step').select('Upload Document');
    cy.get('.button:nth-child(2)').click();
    cy.wait(1000)
    cy.get('.panel > .button').click();
    cy.get('#documentCollection_0_typeOfDocument').select('ET1');
    cy.get('input[type="file"]').attachFile('files/ECMTestDoc.docx')
    cy.get('#documentCollection_0_shortDescription').type('Test');
    cy.wait(1000)
    cy.get('.form-group > .button:nth-child(2)').click();
    cy.get('.form').submit();
    cy.wait(1000)
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
})