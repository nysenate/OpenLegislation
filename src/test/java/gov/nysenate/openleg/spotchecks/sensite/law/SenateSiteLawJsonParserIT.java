package gov.nysenate.openleg.spotchecks.sensite.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SenateSiteLawJsonParserIT extends BaseTests {

    @Autowired private SenateSiteLawJsonParser lawJsonParser;

    @Test
    public void parseLawDumpFragment() {
        final String testFilePath = "spotcheck/senatesite/law/json-parser/senate-site-laws_dump-20190404T133502-038.json";
        File resourceFile = FileIOUtils.getResourceFile(testFilePath);
        SenateSiteDumpId dumpId = new SenateSiteDumpId(
                SpotCheckRefType.SENATE_SITE_LAW, 134, 2019, LocalDateTime.parse("2019-04-04T13:35:02")
        );
        SenateSiteDumpFragment fragment = new SenateSiteDumpFragment(dumpId, 38, resourceFile);
        SenateSiteLawChapter senateSiteLawChapter = lawJsonParser.parseLawDumpFragment(fragment);
        assertEquals("SLG", senateSiteLawChapter.getLawId());
        assertEquals(13, senateSiteLawChapter.getDocuments().size());

        SenateSiteLawDoc senateSiteLawDoc = senateSiteLawChapter.getDocuments().get(5);
        assertEquals("SLGA2", senateSiteLawDoc.getStatuteId());
        assertEquals("Grants of Powers To Local Governments", senateSiteLawDoc.getTitle());
        assertEquals("/legislation/laws/SLG/A3", senateSiteLawDoc.getNextSiblingUrl());
        assertEquals(LocalDate.of(2014, 9, 22).atStartOfDay(), senateSiteLawDoc.getActiveDate());

    }
}