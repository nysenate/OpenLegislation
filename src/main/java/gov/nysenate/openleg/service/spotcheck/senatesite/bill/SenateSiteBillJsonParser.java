package gov.nysenate.openleg.service.spotcheck.senatesite.bill;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.bill.BillActionView;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillStatusView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.bill.BillVoteType;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBillVote;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.SenateSiteJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillVoteCode.*;

@Service
public class SenateSiteBillJsonParser extends SenateSiteJsonParser {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteBillJsonParser.class);

    public List<SenateSiteBill> parseBills(SenateSiteDump billDump) throws ParseError {
        return billDump.getDumpFragments().stream()
                .flatMap(fragment -> extractBillsFromFragment(fragment).stream())
                .collect(Collectors.toList());
    }

    public List<SenateSiteBill> extractBillsFromFragment(SenateSiteDumpFragment fragment) throws ParseError {
        logger.info("Parsing bills from NYSenate.gov dump fragment: {}", fragment.getFragmentFile().getName());
        try {
            JsonNode billMap = objectMapper.readTree(fragment.getFragmentFile())
                    .path("nodes");
            if (billMap.isMissingNode()) {
                throw new ParseError("Could not locate \"nodes\" node in senate site bill dump fragment file: " +
                        fragment.getFragmentFile().getAbsolutePath());
            }
            List<SenateSiteBill> bills = new LinkedList<>();
            for (JsonNode billNode : billMap) {
                bills.add(extractSenSiteBill(billNode, fragment));
            }
            return bills;
        } catch (Exception ex) {
            throw new ParseError("error while reading senate site bill dump fragment file: " +
                    fragment.getFragmentFile().getAbsolutePath(),
                    ex);
        }
    }

    /* --- Internal Methods --- */

    private SenateSiteBill extractSenSiteBill(JsonNode billNode, SenateSiteDumpFragment fragment) {
        SenateSiteBill bill = new SenateSiteBill(fragment.getDumpId().getDumpTime());

        final String printNo = getValue(billNode, "field_ol_print_no");
        bill.setPrintNo(printNo);

        try {
            bill.setBasePrintNo(getValue(billNode, "field_ol_base_print_no"));
            bill.setActiveVersion(getValue(billNode, "field_ol_active_version"));
            bill.setMilestones(getMilestones(billNode, "field_ol_all_statuses"));
            bill.setChamber(getValue(billNode, "field_ol_chamber"));
            bill.setCoSponsors(getMembers(billNode, "field_ol_co_sponsor_names"));
            bill.setText(getValue(billNode, "field_ol_full_text"));
            bill.setAmended(getBooleanValue(billNode, "field_ol_is_amended"));
            bill.setLatestStatusCommittee(getValue(billNode, "field_ol_latest_status_committee"));
            bill.setLawCode(getValue(billNode, "field_ol_law_code"));
            bill.setLawSection(getValue(billNode, "field_ol_law_section"));
            bill.setMemo(getValue(billNode, "field_ol_memo"));
            bill.setMultiSponsors(getMembers(billNode, "field_ol_multi_sponsor_names"));
            bill.setTitle(getValue(billNode, "field_ol_name"));
            bill.setPreviousVersions(getBillIdList(billNode, "field_ol_previous_versions"));
            bill.setPublishDate(parseUnixTimeValue(billNode, "field_ol_publish_date"));
            bill.setSameAs(getBillIdList(billNode, "field_ol_same_as"));
            bill.setSponsor(getValue(billNode, "field_ol_sponsor_name"));
            bill.setSummary(getValue(billNode, "field_ol_summary"));
            bill.setSessionYear(getIntValue(billNode, "field_ol_session"));
            bill.setLastStatus(getValue(billNode, "field_ol_last_status"));
            bill.setLastStatusDate(parseUnixTimeValue(billNode, "field_ol_last_status_date"));
            bill.setActions(getActionList(billNode, "field_ol_all_actions"));
            bill.setHasSameAs(getBooleanValue(billNode, "field_ol_has_same_as"));
            bill.setVotes(getVotes(billNode, bill.getBillId()));

            if (bill.getBaseBillId().getBillType().isResolution()) {
                // Public Website has different models for resolution and bills. For resolutions action info is stored
                // in the field_ol_all_statuses node.
                bill.setActions(getActionList(billNode, "field_ol_all_statuses"));
            }

            return bill;
        } catch (Exception ex) {
            throw new ParseError("Error while parsing senate site bill " + printNo, ex);
        }
    }

    private List<BillStatusView> getMilestones(JsonNode billNode, String fieldName) {
        TypeReference<ListView<BillStatusView>> billStatusListType = new TypeReference<ListView<BillStatusView>>() {};
        Optional<ListView<BillStatusView>> billStatusList = deserializeValue(billNode, fieldName, billStatusListType);
        return billStatusList.map(ListView::getItems).orElse(ImmutableList.of());
    }

    private List<String> getMembers(JsonNode billNode, String memberFieldName) {
        TypeReference<List<MemberView>> memberListType = new TypeReference<List<MemberView>>() {};
        Optional<List<MemberView>> memberList = deserializeValue(billNode, memberFieldName, memberListType);
        return memberList.orElse(Collections.emptyList()).stream()
                .map(MemberView::getShortName)
                .collect(Collectors.toList());
    }

    private List<BillId> getBillIdList(JsonNode billNode, String fieldName) {
        TypeReference<List<BillIdView>> billIdListType = new TypeReference<List<BillIdView>>() {};
        Optional<List<BillIdView>> billIdViews = deserializeValue(billNode, fieldName, billIdListType);
        return billIdViews.orElse(Collections.emptyList()).stream()
                .map(BillIdView::toBillId)
                .collect(Collectors.toList());
    }

    private List<BillAction> getActionList(JsonNode billNode, String fieldName) {
        TypeReference<ListView<BillActionView>> actionListType = new TypeReference<ListView<BillActionView>>() {};
        Optional<ListView<BillActionView>> actionListView = deserializeValue(billNode, fieldName, actionListType);
        return actionListView
                .map(ListView::getItems)
                .orElse(ImmutableList.of()).stream()
                .map(BillActionView::toBillAction)
                .collect(Collectors.toList());
    }

    private List<SenateSiteBillVote> getVotes(JsonNode billNode, BillId billId) {
        List<JsonNode> jsonVotes = getListValue(billNode, "field_ol_votes", Function.identity());
        return jsonVotes.stream()
                .filter(JsonNode::isObject)
                .map(voteNode -> parseVote(voteNode, billId))
                .collect(Collectors.toList());
    }

    private SenateSiteBillVote parseVote(JsonNode voteNode, BillId billId) {
        BillVoteType type = getEnumValue(voteNode, "field_ol_vote_type", BillVoteType.class);
        LocalDate voteDate = parseTimeStamp(voteNode, "field_publication_date").toLocalDate();
        String committeeName = getValue(voteNode, "field_ol_committee");
        Map<BillVoteCode, Integer> voteCounts = new EnumMap<>(BillVoteCode.class);
        Multimap<BillVoteCode, Integer> voteRoll = LinkedListMultimap.create();

        for (BillVoteCode code : BillVoteCode.values()) {
            String fieldPrefix = "field_ol_" + getVoteCodeParamName(code);
            String countField = fieldPrefix + "_count";
            String membersField = fieldPrefix + "_members";

            int count = getIntValue(voteNode, countField);
            List<Integer> memberIds = getListValue(voteNode, membersField, JsonNode::asInt);

            voteCounts.put(code, count);
            voteRoll.putAll(code, memberIds);
        }

        return new SenateSiteBillVote(
                billId,
                type,
                voteDate,
                committeeName,
                voteCounts,
                voteRoll
        );
    }

    private static final ImmutableMap<BillVoteCode, String> voteCodeFieldMap =
            ImmutableMap.<BillVoteCode, String>builder()
                    .put(AYE, "aye")
                    .put(AYEWR, "aye_wr")
                    .put(EXC, "excused")
                    .put(NAY, "nay")
                    .put(ABD, "abstained")
                    .put(ABS, "absent")
                    .build();

    private String getVoteCodeParamName(BillVoteCode code) {
        return Optional.ofNullable(voteCodeFieldMap.get(code))
                .orElseThrow(() -> new IllegalArgumentException("No known parameter for vote code: " + code));
    }

}
