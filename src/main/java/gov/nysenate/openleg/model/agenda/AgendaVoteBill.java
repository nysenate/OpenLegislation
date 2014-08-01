package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.Member;

import java.io.Serializable;
import java.util.List;

public class AgendaVoteBill implements Serializable
{
    private static final long serialVersionUID = 8418895620868449773L;

    private BillId billId;

    private AgendaVoteAction voteAction;

    private CommitteeId referCommittee;

    private boolean withAmendment;

    private List<BillVoteCode, Member> memberVotes;

}
