package gov.nysenate.openleg.legislation.bill.dao.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.updates.calendar.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.updates.calendar.CalendarUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Evicts bills from the cached bill data service cache based on events
 */
@Service
public class BillCacheEvictionService {
    private final EventBus eventBus;
    private final CachedBillDataService billDataService;

    @Autowired
    public BillCacheEvictionService(EventBus eventBus, CachedBillDataService billDataService) {
        this.eventBus = eventBus;
        this.billDataService = billDataService;
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    public void handleCalendarUpdate(CalendarUpdateEvent calendarUpdateEvent) {
        evictCalendarBills(calendarUpdateEvent.calendar());
    }

    @Subscribe
    public void handleBulkCalendarUpdate(BulkCalendarUpdateEvent bulkCalendarUpdateEvent) {
        bulkCalendarUpdateEvent.calendars().forEach(this::evictCalendarBills);
    }

    @Subscribe
    public void handleAgendaUpdate(AgendaUpdateEvent agendaUpdateEvent) {
        evictAgendaBills(agendaUpdateEvent.agenda());
    }

    @Subscribe
    public void handleBulkAgendaUpdate(BulkAgendaUpdateEvent bulkAgendaUpdateEvent) {
        bulkAgendaUpdateEvent.agendas().forEach(this::evictAgendaBills);
    }

    private void evictCalendarBills(Calendar calendar) {
        calendar.getSupplementalMap().values().stream()
                .flatMap(calSup -> calSup.getAllEntries().stream())
                .map(calSupEntry -> BillId.getBaseId(calSupEntry.getBillId()))
                .forEach(billDataService::evictBill);
    }

    private void evictAgendaBills(Agenda agenda) {
        agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(addendum -> addendum.getCommitteeInfoMap().values().stream())
                .flatMap(commInfo -> commInfo.getItems().stream())
                .map(infCommItem -> BillId.getBaseId(infCommItem.getBillId()))
                .forEach(billDataService::evictBill);
        agenda.getAgendaVoteAddenda().values().stream()
                .flatMap(voteAdd -> voteAdd.getCommitteeVoteMap().values().stream())
                .flatMap(voteComm -> voteComm.getVotedBills().keySet().stream())
                .map(BillId::getBaseId)
                .forEach(billDataService::evictBill);
    }
}
