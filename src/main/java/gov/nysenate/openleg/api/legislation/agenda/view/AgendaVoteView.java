package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;

public record AgendaVoteView(ListView<AgendaAttendanceView> attendanceList,
                             ListView<AgendaVoteBillView> votesList) implements ViewObject {
    public AgendaVoteView(AgendaVoteCommittee voteComm) {
        this(ListView.of(voteComm.getAttendance().stream().map(AgendaAttendanceView::new).toList()),
                ListView.of(voteComm.getVotedBills().values().stream().map(AgendaVoteBillView::new).toList()));
    }

    @Override
    public String getViewType() {
        return "agenda-vote";
    }
}
