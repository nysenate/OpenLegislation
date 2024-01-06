package gov.nysenate.openleg.api.legislation.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoAddendum;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgendaBillUtils {
    private final BillDataService billData;

    @Autowired
    public AgendaBillUtils(BillDataService billData) {
        this.billData = billData;
    }

    public Map<AgendaInfoCommittee, List<AgendaBillInfo>> getBillInfoMap(Agenda agenda, CommitteeId committeeId) {
        List<AgendaInfoCommittee> infoComms = agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(infoAddendum -> getInfoCommittees(infoAddendum, committeeId).stream())
                .toList();
        var infoCommMap = new HashMap<AgendaInfoCommittee, List<AgendaBillInfo>>();
        for (var infoComm : infoComms) {
            infoCommMap.put(infoComm, infoComm.getItems().stream()
                    .map(this::getAgendaBillInfo).toList());
        }
        return infoCommMap;
    }

    private static Collection<AgendaInfoCommittee> getInfoCommittees(AgendaInfoAddendum infoAddendum, CommitteeId id) {
        if (id == null) {
            return infoAddendum.getCommitteeInfoMap().values();
        }
        var comm = infoAddendum.getCommittee(id);
        return comm == null ? List.of() : List.of(comm);
    }

    private AgendaBillInfo getAgendaBillInfo(AgendaInfoCommitteeItem item) {
        var billInfo = billData.getBillInfoSafe(BaseBillId.of(item.getBillId()));
        return new AgendaBillInfo(item.getBillId(), billInfo, item.getMessage());
    }
}
