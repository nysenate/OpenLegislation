package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class AgendaVoteView implements ViewObject
{
    private ListView<AgendaAttendanceView> attendanceList;
    private ListView<AgendaVoteBillView> votesList;

    public AgendaVoteView(AgendaVoteCommittee voteComm) {
        if (voteComm != null) {
            this.attendanceList = ListView.of(voteComm.getAttendance().stream()
                .map(a -> new AgendaAttendanceView(a)).collect(toList()));
            this.votesList = ListView.of(voteComm.getVotedBills().values().stream()
                .map(v -> new AgendaVoteBillView(v)).collect(Collectors.toList()));
        }
    }

    //Added for Json Deserialization
    public AgendaVoteView() {}

    public ListView<AgendaAttendanceView> getAttendanceList() {
        return attendanceList;
    }

    public ListView<AgendaVoteBillView> getVotesList() {
        return votesList;
    }

    //Added for Json Deserialization
    public void setAttendanceList(ListView<AgendaAttendanceView> attendanceList) {this.attendanceList = attendanceList;}

    public void setVotesList(ListView<AgendaVoteBillView> votesList) {this.votesList = votesList;}

    @Override
    public String getViewType() {
        return "agenda-vote";
    }
}
