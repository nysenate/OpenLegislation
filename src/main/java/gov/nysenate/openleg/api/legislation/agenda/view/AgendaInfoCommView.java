package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class AgendaInfoCommView implements ViewObject
{
    private CommitteeIdView committee;
    private String chair;
    private String location;
    private LocalDateTime meetingDateTime;
    private String notes;
    private ListView<AgendaItemView> bills;

    public AgendaInfoCommView(AgendaInfoCommittee infoComm, BillDataService billDataService) {
        if (infoComm != null) {
            this.committee = new CommitteeIdView(infoComm.getCommitteeId());
            this.chair = infoComm.getChair();
            this.location = infoComm.getLocation();
            this.meetingDateTime = infoComm.getMeetingDateTime();
            this.notes = infoComm.getNotes();
            this.bills = ListView.of(infoComm.getItems().stream()
                    .map(i -> new AgendaItemView(i, billDataService))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public String getViewType() {
        return "agenda-info-committee";
    }
}
