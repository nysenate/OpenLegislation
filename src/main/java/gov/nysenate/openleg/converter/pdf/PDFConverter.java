package gov.nysenate.openleg.converter.pdf;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Transcript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import gov.nysenate.openleg.util.TranscriptLine;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PDFConverter
{
    // Kirkland started on May 16, 2011
    protected static long KIRKLAND_START_TIME = 1305086400000L;

    // Candyco started Jan 1st, 2005
    private static long CANDYCO_START_TIME = 1104555600000L;

    // Candyco also did 1999-2003
    private static long CANDYCO_1999_START = 915166800000L;
    private static long CANDYCO_2003_END = 1072932900000L;

    // Pauline Williman did 1993-1998
    private static long WILLIMAN_START = 725864400000L;
    private static long WILLIMAN_END = 915166500000L;

    private static Float bot = 90f;
    private static Float right = 575f;
    private static Float top = 710f;
    private static Float left = 105f;
    private static Float fontSize = 12f;
    private static Float fontWidth = 7f;

    public static final int NO_LINE_NUM_INDENT = 11;

    public static void write(IBaseObject object, OutputStream out) throws IOException, COSVisitorException, ApiRequestException
    {
        if (object instanceof Transcript) {
            PDFConverter.write((Transcript)object, out);
        }
        else {
            throw new ApiRequestException("Unable to convert "+object.getOtype()+"s to pdf.");
        }
    }

    public static void write(Transcript transcript, OutputStream out) throws IOException, COSVisitorException
    {
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;

        List<TranscriptPage> pages = new TranscriptPageParser().parsePages(transcript);

        for (TranscriptPage page : pages) {
            PDPage pg = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
            
            drawBorder(contentStream);
            
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            drawPageText(page, contentStream);
            drawStenographer(transcript, page, contentStream);

            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(out);
        doc.close();
    }

    private static void drawPageText(TranscriptPage page, PDPageContentStream contentStream) throws IOException {
        if (page.getTranscriptNumber() == null) {
            contentStream.moveTextPositionByAmount(0, top - fontWidth);
        } else {
            float offset = right - (page.getTranscriptNumber().length() + 1) * fontWidth;
            drawTranscriptNumber(page.getTranscriptNumber(), offset, contentStream);
        }

        for (TranscriptLine line : page.getLines()) {
            int indent;
            String text;
            if (line.hasLineNumber()) {
                indent = lineNumberLength(line) + 1;
                text = line.fullText().trim();
            }
            else {
                indent = NO_LINE_NUM_INDENT;
                text = line.fullText();
            }

            float offset = left - indent * fontWidth;
            drawLine(text, offset, contentStream);
        }
    }

    private static int lineNumberLength(TranscriptLine line) {
        return line.fullText().trim().split("\\s")[0].length();
    }

    private static void drawLine(String line, float offset, PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(offset, -fontSize);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize);
    }

    private static void drawTranscriptNumber(String line, float offset, PDPageContentStream contentStream) throws IOException {
        contentStream.moveTextPositionByAmount(offset, top + fontWidth);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize * 2);
    }

    private static void drawStenographer(Transcript transcript, TranscriptPage page, PDPageContentStream contentStream) throws IOException {
        String stenographer;
        if (transcript.getTimeStamp().getTime() >= KIRKLAND_START_TIME) {
            stenographer = "Kirkland Reporting Service";
        }
        else if (transcript.getTimeStamp().getTime() >= CANDYCO_START_TIME) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getTimeStamp().getTime() >= CANDYCO_1999_START && transcript.getTimeStamp().getTime() <= CANDYCO_2003_END) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getTimeStamp().getTime() >= WILLIMAN_START && transcript.getTimeStamp().getTime() <= WILLIMAN_END) {
            stenographer = "Pauline Williman, Certified Shorthand Reporter";
        }
        else {
            stenographer = "";
        }

        // 27 because page# + 25 lines + 1 line stenographer offset
        float offset = (page.getLineCount()-27)*2*fontSize;
        contentStream.moveTextPositionByAmount(left+ (right-left-stenographer.length()*fontWidth)/2, offset);
        contentStream.drawString(stenographer);
    }

    private static void drawBorder(PDPageContentStream contentStream) throws IOException {
        contentStream.drawLine(left, top, left, bot);
        contentStream.drawLine(left, top, right, top);
        contentStream.drawLine(left, bot, right, bot);
        contentStream.drawLine(right, top, right, bot);
    }
}
