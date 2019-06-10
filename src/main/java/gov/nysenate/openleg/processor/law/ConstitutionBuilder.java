package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for the special cases of the Constitution, Senate Rules, and Assembly Rules.
 */
public class ConstitutionBuilder extends IdBasedLawBuilder implements LawBuilder {
    private static final String CONS_STR = "CNS";
    // The title of article x section y can be found at sectionTitles.get(x).get(y).
    private ArrayList<List<String>> allSectionTitles = new ArrayList<>();

    public ConstitutionBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    protected void addRootDocument(LawDocument rootDoc, boolean isNewDoc) {
        super.addRootDocument(rootDoc, isNewDoc);
        String[] articles = rootDoc.getText().split("(\\w*ARTICLE [IVX]*\n\\w*)|\\*");
        // The -1 adjustment is due to a ote with a star at the end of this document.
        for (int i = 1; i < articles.length-1; i++) {
            String[] curr = articles[i].split("\\w*\\d*. ");
            // Correct the article name.
            curr[0] = curr[0].split("\n", 1)[0];
            LawDocInfo articleInfo = new LawDocInfo(CONS_STR + "A" + i,
                    CONS_STR, "A" + i, curr[0], LawDocumentType.ARTICLE,
                    "A" + i, rootDoc.getPublishedDate());
            super.addDocument(new LawDocument(articleInfo, "IDK"), isNewDoc);
            allSectionTitles.add(Arrays.asList(curr));
        }
    }
}
