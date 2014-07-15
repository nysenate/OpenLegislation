package gov.nysenate.openleg.model.bill;

/**
 * Represents the possible voting code prefixes.
 */
public enum BillVoteCode
{
    AYE,   // Voted 'Yes'
    NAY,   // Voted 'No'
    EXC,   // Excused from Vote
    ABS,   // Absent from Vote
    ABD,   // Abstained from Vote
    AYEWR  // Vote 'Yes, with reservations'
}
