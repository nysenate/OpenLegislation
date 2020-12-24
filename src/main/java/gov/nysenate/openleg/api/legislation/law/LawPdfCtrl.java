package gov.nysenate.openleg.api.legislation.law;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.legislation.law.view.LawDocQueryView;
import gov.nysenate.openleg.api.legislation.law.view.LawIdQueryView;
import gov.nysenate.openleg.api.legislation.law.view.LawPdfView;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawTree;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawDocumentNotFoundEx;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/pdf/laws")
public class LawPdfCtrl extends BaseCtrl {
    private static final Pattern DOCUMENT_ID_PATTERN = Pattern.compile("(?i)([A-Z]{3})(.*)");

    @Autowired
    private LawDataService lawData;

    /**
     *
     * @param documentId of the law document to look up. If you want the root
     * node, enter just the 3 letter law id instead.
     * @param full if you want the children to be shown.
     * @return a law document PDF.
     * @throws IOException if PDF cannot be written.
     */
    @RequestMapping("/{documentId}")
    public ResponseEntity<byte[]> getTranscriptPdf(@PathVariable String documentId,
                                                   @RequestParam(defaultValue = "false") boolean full)
            throws IOException, COSVisitorException {
        Matcher matcher = DOCUMENT_ID_PATTERN.matcher(documentId);
        if (!matcher.matches())
            throw new InvalidRequestParamEx(documentId, "documentId", "String", "Document ID must start with a 3 letter law ID.");
        LawTree lawTree = lawData.getLawTree(matcher.group(1));
        // This allows full law trees to be obtained.
        if (matcher.group(2).isEmpty())
            documentId = lawTree.getRootNode().getDocumentId();
        Optional<LawTreeNode> opNode = lawTree.find(documentId);
        if (!opNode.isPresent())
            throw new LawDocumentNotFoundEx(documentId, null, "");

        Map<String, LawDocument> lawDocs = new HashMap<>();
        lawDocs.put(documentId, lawData.getLawDocument(documentId, null));
        if (full) {
            for (LawTreeNode node : opNode.get().getAllNodes())
                lawDocs.put(node.getDocumentId(), lawData.getLawDocument(node.getDocumentId(), null));
        }

        ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
        LawPdfView.writeLawDocumentPdf(opNode.get(), pdfBytes, lawDocs);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
    }

    @ExceptionHandler(LawTreeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawTreeNotFoundEx(LawTreeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_TREE_NOT_FOUND, new LawIdQueryView(ex.getLawId(), ex.getEndPubDate()));
    }

    @ExceptionHandler(LawDocumentNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawDocNotFoundEx(LawDocumentNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_DOC_NOT_FOUND, new LawDocQueryView(ex.getDocId(), ex.getEndPublishedDate()));
    }
}
