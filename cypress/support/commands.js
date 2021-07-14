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
const date = new Date();

Cypress.Commands.add('aatLogin', () => {
    cy.visit(Cypress.env('aatURL'))
    cy.get('#username').type('eric.ccdcooper@gmail.com').should('have.value', 'eric.ccdcooper@gmail.com');
    cy.get('#password').type('Nagoya0102').should('have.value', 'Nagoya0102');
    cy.wait(1000)
    cy.get('.button').click()
    cy.wait(5000);
})

Cypress.Commands.add('rejectCase' , () => {
    cy.get('#next-step').select('Accept/Reject Case');
    cy.get('.button:nth-child(2)').click();
    cy.get('.event-trigger').submit();
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
    cy.get('.event-trigger').submit();
    cy.get('#preAcceptCase_caseAccepted_Yes').click();
    cy.get('#dateAccepted-day').type(date.getDate().toString());
    cy.get('#dateAccepted-month').type((date.getMonth()+1).toString());
    cy.get('#dateAccepted-year').type(date.getFullYear().toString());
    cy.get('.button:nth-child(2)').click();
    cy.get('.form').submit();
    // Submit page
    cy.get('.button:nth-child(2)').click();
    cy.get('.check-your-answers').submit();
    cy.wait(5000)
})