package gov.nysenate.openleg.spotchecks.mismatch;

import gov.nysenate.openleg.spotchecks.model.DeNormSpotCheckMismatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.web.socket.sockjs.transport.handler.HtmlFileTransportHandler;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.mismatch.WhitespaceOption.*;


/**
 * This class generates html diffs for a spotcheck mismatch.
 */
public final class MismatchHtmlDiff {

    private final DeNormSpotCheckMismatch mismatch;
    private final DiffMatchPatch dmp;
    private LinkedList<DiffMatchPatch.Diff> diffs;
    private final WhitespaceOption whitespaceOption;
    private final Set<CharacterOption> characterOptions;

    public MismatchHtmlDiff(DeNormSpotCheckMismatch mismatch, WhitespaceOption whitespaceOption,
                            Set<CharacterOption> characterOptions) {
        this.mismatch = mismatch;
        this.dmp = new DiffMatchPatch();
        this.whitespaceOption = whitespaceOption;
        this.characterOptions = characterOptions;
    }

    /**
     * Returns html representing the combined diff, both additions and deletions are shown in a single view.
     * This can be used to display the diffs in a single view (instead of side by side).
     */
    public String combinedHtmlDiff() {
        var diffs = getDiffs();
        var html = new StringBuilder();
        for (DiffMatchPatch.Diff aDiff : diffs) {
            String text = htmlEncodeText(aDiff);
            switch (aDiff.operation) {
                case INSERT -> html.append(insertHtml(text));
                case DELETE -> html.append(deleteHtml(text));
                case EQUAL -> html.append(equalHtml(text));
            }
        }
        return html.toString();
    }

    /**
     * Returns the html representing the observed diff. Only observed text is included.
     * If the observed text has data that does not exist in the reference text, it will be styled as inserted text.
     * This is used to show the observed text diffs in a side by side comparison.
     */
    public String observedHtmlDiff() {
        var diffs = getDiffs();
        var html = new StringBuilder();
        for (DiffMatchPatch.Diff aDiff : diffs) {
            String text = htmlEncodeText(aDiff);
            switch (aDiff.operation) {
                case INSERT -> html.append(insertHtml(text));
                case EQUAL -> html.append(equalHtml(text));
            }
        }
        return html.toString();
    }

    /**
     * Returns the html representing the reference diff. Only reference text is included.
     * If the reference text has data that does not exist in the observed text, it will be styled as deleted text.
     * This is used to show the reference text diffs in a side by side comparison.
     */
    public String referenceHtmlDiff() {
        var diffs = getDiffs();
        var html = new StringBuilder();
        for (DiffMatchPatch.Diff aDiff : diffs) {
            String text = htmlEncodeText(aDiff);
            switch (aDiff.operation) {
                case DELETE -> html.append(deleteHtml(text));
                case EQUAL -> html.append(equalHtml(text));
            }
        }
        return html.toString();
    }

    private String htmlEncodeText(DiffMatchPatch.Diff aDiff) {
        return aDiff.text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String insertHtml(String text) {
        return "<ins style=\"background:#e6ffe6;\">" + text + "</ins>";
    }

    private String deleteHtml(String text) {
        return "<del style=\"background:#ffe6e6;\">" + text + "</del>";
    }

    private String equalHtml(String text) {
        return "<span>" + text + "</span>";
    }

    /**
     * If diffs has not yet been generated, it is generated and returned.
     * Otherwise, it returns the previously generated diffs.
     */
    private LinkedList<DiffMatchPatch.Diff> getDiffs() {
        if (this.diffs != null) {
            // Diffs were already created. We can use them again.
            return this.diffs;
        }

        // Normalize line endings.
        String formattedObservedData = HtmlDiffFormatter.normalizeLineEndings(this.mismatch.getObservedData());
        String formattedReferenceData = HtmlDiffFormatter.normalizeLineEndings(this.mismatch.getReferenceData());

        // Apply whitespace options.
        formattedObservedData = HtmlDiffFormatter.applyWhitespaceOption(formattedObservedData, this.whitespaceOption);
        formattedReferenceData = HtmlDiffFormatter.applyWhitespaceOption(formattedReferenceData, this.whitespaceOption);

        // Apply Character options.
        for (CharacterOption opt : this.characterOptions) {
            formattedObservedData = HtmlDiffFormatter.applyCharacterOption(formattedObservedData, opt);
            formattedReferenceData = HtmlDiffFormatter.applyCharacterOption(formattedReferenceData, opt);
        }

        // Create and return diffs
        this.diffs = dmp.diffMain(formattedObservedData, formattedReferenceData);
        dmp.diffCleanupSemantic(this.diffs);
        return this.diffs;
    }
}
