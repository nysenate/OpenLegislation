package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.client.view.agenda.AgendaItemView;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgendaBill;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.JsonParser;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class AgendaJsonParser extends JsonParser {
    @Autowired
    ObjectMapper objectMapper;

    public List<SenateSiteAgenda> parseAgendas(SenateSiteDump agendaDump) throws ParseError {
        return agendaDump.getDumpFragments().stream()
                .flatMap(fragment -> extractAgendasFromFragment(fragment).stream())
                .collect(Collectors.toList());
    }

    private List<SenateSiteAgenda> extractAgendasFromFragment(SenateSiteDumpFragment fragment) throws ParseError{
        try {
            JsonNode agendaMap = objectMapper.readTree(fragment.getFragmentFile())
                    .path("nodes");
            if (agendaMap.isMissingNode()) {
                throw new ParseError("Could not locate \"nodes\" node in senate site agenda dump fragment file: " +
                        fragment.getFragmentFile().getAbsolutePath());
            }
            List<SenateSiteAgenda> agendas = new LinkedList<>();
            for (JsonNode agendaNode : agendaMap) {
                agendas.add(extractSenSiteAgenda(agendaNode, fragment));
            }
            return agendas;
        } catch (IOException | NoSuchElementException ex) {
            throw new ParseError("error while reading senate site agenda dump fragment file: " +
                    fragment.getFragmentFile().getAbsolutePath(),
                    ex);
        }
    }

    private SenateSiteAgenda extractSenSiteAgenda(JsonNode agendaNode, SenateSiteDumpFragment fragment) throws IOException {
        SenateSiteAgenda agenda = new SenateSiteAgenda(DateUtils.endOfDateTimeRange(fragment.getDumpId().getRange()));
        agenda.setAgendaId(new AgendaId(getIntValue(agendaNode,"field_ol_week"),getIntValue(agendaNode,"field_ol_year")));
        agenda.setCommittee(new CommitteeId(Chamber.SENATE, getValue(agendaNode,"field_ol_committee_name")));
        agenda.setAddendum((getValue(agendaNode,"field_ol_agenda_addendum") != null) ? getValue(agendaNode,"field_ol_agenda_addendum") : "");
        List<String> nodes = getStringListValue(agendaNode,"field_ol_agenda_bills");
        List<SenateSiteAgendaBill> agendaBills = (nodes != null) ? getAgendaBills(nodes) : null;
        agenda.setAgendaBills(agendaBills);
        return agenda;
    }

    private List<SenateSiteAgendaBill> getAgendaBills(List<String> agendaBillsStrings) throws IOException {
        List<SenateSiteAgendaBill> agendaBills = new ArrayList<>();
        for (String agendaBill : agendaBillsStrings){
            JsonNode node = objectMapper.readTree(agendaBill);
            SenateSiteAgendaBill ssAgendaBill = new SenateSiteAgendaBill();
            ssAgendaBill.setAbstainedCount(getIntValue(node,"field_ol_abstained_count"));
            ssAgendaBill.setAyeCount(getIntValue(node,"field_ol_aye_count"));
            ssAgendaBill.setAyeWrCount(getIntValue(node,"field_ol_aye_wr_count"));
            ssAgendaBill.setBillMessage(getValue(node,"field_ol_bill_message"));
            ssAgendaBill.setExcusedCount(getIntValue(node,"field_ol_excused_count"));
            ssAgendaBill.setNayCount(getIntValue(node,"field_ol_nay_count"));
            TypeReference<AgendaItemView> type = new TypeReference<AgendaItemView>() {};
            ssAgendaBill.setBillName(deserializeValue(node,"field_ol_bill_name",type).get());
            agendaBills.add(ssAgendaBill);
        }
        return agendaBills;
    }
}
