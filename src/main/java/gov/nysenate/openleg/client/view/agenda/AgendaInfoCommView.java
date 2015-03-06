package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.service.bill.data.BillDataService;

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
