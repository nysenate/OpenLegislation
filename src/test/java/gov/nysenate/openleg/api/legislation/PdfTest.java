package gov.nysenate.openleg.api.legislation;

import gov.nysenate.openleg.api.ApiTest;
import gov.nysenate.openleg.api.legislation.law.LawPdfCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.HearingGetCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.TranscriptGetCtrl;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.law.LawInfo;
import gov.nysenate.openleg.legislation.law.dao.LawDataDao;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDao;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SillyTest
public class PdfTest extends ApiTest {
    @Autowired
    private LawPdfCtrl lawPdfCtrl;
    @Autowired
    private LawDataDao lawDataDao;

    @Autowired
    private TranscriptGetCtrl transcriptGetCtrl;
    @Autowired
    private TranscriptDao transcriptDao;

    @Autowired
    private HearingGetCtrl hearingGetCtrl;
    @Autowired
    private HearingDao hearingDao;

    // TODO: bill tests
    @Test
    public void fullLawTest() throws IOException {
        Set<String> badDocIds = new HashSet<>();
        Set<Integer> badCodePoints = new HashSet<>();
        int docIdCount = 0;
        var lawIds = lawDataDao.getLawInfos().stream().map(LawInfo::getLawId).toList();
        for (var lawId : lawIds) {
            var lawTree = lawDataDao.getLawTree(lawId, LocalDate.now());
            for (var docNode : lawTree.getRootNode().getAllNodes()) {
                docIdCount++;
                var doc = lawDataDao.getLawDocument(docNode.getDocumentId(), LocalDate.now());
                for (int codePoint : doc.getText().codePoints().toArray()) {
                    if (notPrintable(codePoint)) {
                        badDocIds.add(docNode.getDocumentId());
                        badCodePoints.add(codePoint);
                    }
                }
            }
            //            lawPdfCtrl.getLawPdf(lawId, true);
        }
        System.out.println(badDocIds);
        System.out.println(badCodePoints);
    }

    @Test
    public void fullSessionTest() throws IOException {
        int limit = 500;
        var ids = transcriptDao.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (int i = 0; i < ids.size(); i++) {
            transcriptGetCtrl.getTranscriptPdf(ids.get(i).getDateTime().toString());
            if (i%limit == 0)
                System.out.println(limit + " done!");
        }
    }

    @Test
    public void fullHearingTest() throws IOException {
        var ids = hearingDao.getHearingIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids)
            hearingGetCtrl.getHearingPdf(String.valueOf(id.id()));
    }

    static Set<Integer> alphaNum = new HashSet<>(), punctuation = new HashSet<>(), accentChars = new HashSet<>();
    static {
        for (int i = 'a'; i <= 'z'; i++)
            alphaNum.add(i);
        for (int i = 'A'; i <= 'Z'; i++)
            alphaNum.add(i);
        for (int i = '0'; i <= '9'; i++)
            alphaNum.add(i);
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", ":", "-", "'", "(", ")", "\"", ";", "$", "/",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@",
                "~", "%", "'", "–", "§", "ø", "¶", "±"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "Á", "è", "é", "É", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean notPrintable(int codepoint) {
        return !alphaNum.contains(codepoint) && !accentChars.contains(codepoint) &&
                !punctuation.contains(codepoint);
    }
}
