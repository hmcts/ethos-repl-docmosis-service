const ccdUserType = {
    CASEWORKER: 'Caseworker',
    JUDGE: 'Judge'
};

const eventNames = {
    ACCEPT_CASE: 'Accept/Reject Case',
    REJECT_CASE: 'Accept/Reject Case',
    CASE_DETAILS: 'Case Details',
    PRE_ACCEPTANCE_CASE: 'preAcceptanceCase',
    ACCEPT_REJECTED_CASE:'acceptRejectedCase'

};

const states = {
    SUBMITTED: 'Submitted',
    ACCEPTED: 'Accepted',
    REJECTED: 'Rejected',
    CLOSED: 'Closed',
    TRANSFERRED: 'Transferred'
};

module.exports = {
    ccdUserType,
    eventNames,
    states
};
