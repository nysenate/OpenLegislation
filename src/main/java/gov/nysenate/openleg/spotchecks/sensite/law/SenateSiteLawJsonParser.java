package gov.nysenate.openleg.spotchecks.sensite.law;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.openleg.processors.ParseError;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteJsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SenateSiteLawJsonParser extends SenateSiteJsonParser {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteLawJsonParser.class);

    public SenateSiteLawChapter parseLawDumpFragment(SenateSiteDumpFragment fragment) {
        logger.info("Parsing laws from NYSenate.gov dump fragment: {}", fragment.getFragmentFile().getName());
        try {
            JsonNode nodeMap = objectMapper.readTree(fragment.getFragmentFile())
                    .path("nodes");
            if (nodeMap.isMissingNode()) {
                throw new ParseError("Could not locate \"nodes\" node in senate site law dump fragment file: " +
                        fragment.getFragmentFile().getAbsolutePath());
            }
            List<SenateSiteLawDoc> lawDocs = new LinkedList<>();
            for (JsonNode lawDocNode : nodeMap) {
                lawDocs.add(extractLawDoc(lawDocNode, fragment));
            }

            String lawChapterCode = getChapterCode(lawDocs, fragment);

            return new SenateSiteLawChapter(lawChapterCode, lawDocs, fragment.getDumpId().getReferenceId());
        } catch (Exception ex) {
            throw new ParseError("error while reading senate site bill dump fragment file: " +
                    fragment.getFragmentFile().getAbsolutePath(),
                    ex);
        }
    }

    private SenateSiteLawDoc extractLawDoc(JsonNode lawDocNode, SenateSiteDumpFragment fragment) {
        SenateSiteLawDoc.Builder docBuilder = SenateSiteLawDoc.builder();

        docBuilder.setReferenceDateTime(fragment.getDumpId().dumpTime())
                .setActiveDate(parseUnixTimeValue(lawDocNode, "field_activedate"))
                .setChapter(getValue(lawDocNode, "field_chapter"))
                .setDocLevelId(getValue(lawDocNode, "field_doclevelid"))
                .setDocType(getValue(lawDocNode, "field_doctype"))
                .setFromSection(getValue(lawDocNode, "field_fromsection"))
                .setLawId(getValue(lawDocNode, "field_lawid"))
                .setLawName(getValue(lawDocNode, "field_lawname"))
                .setLawType(getValue(lawDocNode, "field_lawtype"))
                .setLocationId(getValue(lawDocNode, "field_locationid"))
                .setNextSiblingUrl(getValue(lawDocNode, "field_nextsibling", "url"))
                .setParentLocationIds(getStringListValue(lawDocNode, "field_parentlocationids"))
                .setParentStatuteId(parseStatuteId(getValue(lawDocNode, "field_parentstatuteid")))
                .setPrevSiblingUrl(getValue(lawDocNode, "field_prevsibling", "url"))
                .setRepealed(getBooleanValue(lawDocNode, "field_repealed"))
                .setRepealedDate(parseUnixTimeValue(lawDocNode, "field_repealeddate"))
                .setSequenceNo(getIntValue(lawDocNode, "field_sequenceno"))
                .setStatuteId(parseStatuteId(getValue(lawDocNode, "field_statuteid")))
                .setText(getValue(lawDocNode, "field_text"))
                .setToSection(getValue(lawDocNode, "field_tosection"))
                .setTitle(lawDocNode.path("title").textValue())
        ;

        return docBuilder.build();
    }

    private String getChapterCode(List<SenateSiteLawDoc> lawDocs, SenateSiteDumpFragment fragment) {
        if (lawDocs.isEmpty()) {
            return "<empty fragment #" + fragment.getSequenceNo() + ">";
        }
        return lawDocs.get(0).getLawId();
    }

    /**
     * Convert website statute id to openleg form
     */
    private String parseStatuteId(String statuteId) {
        return Optional.ofNullable(statuteId)
                .map(sid -> sid.replaceAll("(?<=^[A-Z]{3})/", ""))
                .orElse(null);
    }

    private String getStatuteId(JsonNode lawDocNode) {
        return Optional.ofNullable(getValue(lawDocNode, "field_statuteid"))
                .map(sid -> sid.replaceAll("(?<=^[A-Z]{3})/", ""))
                .orElse(null);
    }
}
