package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Transcript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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

    private static Float bot = 90f;
    private static Float right = 575f;
    private static Float top = 710f;
    private static Float left = 105f;
    private static Float fontSize = 12f;
    private static Float fontWidth = 7f;


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

        ArrayList<List<String>> pages;
        pages = parsePages(transcript.getTranscriptText());

        for (List<String> pageLines : pages) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            drawBorder(contentStream);

            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            int lineCount = 0;
            TranscriptLine line;
            for (String aLine : pageLines) {
                line = new TranscriptLine(aLine);

                if (!line.isEmpty()) {
                    if (lineCount == 0) {
                        // If no transcript number on first page, skip it.
                        if (!line.isTranscriptNumber()) {
                            contentStream.moveTextPositionByAmount(0, top - fontWidth);
                        }
                        else if (line.isTranscriptNumber()) {
                            drawTranscriptNumber(contentStream, line);
                        }
                    }
                    else if (line.isTranscriptNumber()) {
                        drawTranscriptNumber(contentStream, line);
                    }
                    else {
                        drawLine(contentStream, line);
                    }
                    lineCount++;
                }

            }

            String stenographer;
            if (transcript.getTimeStamp().getTime() >= KIRKLAND_START_TIME) {
                stenographer = "Kirkland Reporting Service";
            }
            else {
                stenographer = "Candyco Transcription Service, Inc.";
            }

            // 27 because page# + 25 lines + 1 line stenographer offset
            float offset = (lineCount-27)*2*fontSize;
            contentStream.moveTextPositionByAmount(left+ (right-left-stenographer.length()*fontWidth)/2, offset);
            contentStream.drawString(stenographer);

            contentStream.endText();
            contentStream.close();
            doc.addPage(page);
        }
        doc.save(out);
        doc.close();
    }

    private static void drawBorder(PDPageContentStream contentStream) throws IOException {
        contentStream.drawLine(left, top, left, bot);
        contentStream.drawLine(left, top, right, top);
        contentStream.drawLine(left, bot, right, bot);
        contentStream.drawLine(right, top, right, bot);
    }

    private static void drawTranscriptNumber(PDPageContentStream contentStream, TranscriptLine line) throws IOException {
        float offset = right - (line.fullText().trim().length() + 1) * fontWidth;
        contentStream.moveTextPositionByAmount(offset, top + fontWidth);
        contentStream.drawString(line.fullText().trim());
        contentStream.moveTextPositionByAmount(-offset, -fontSize * 2);
    }

    private static void drawLine(PDPageContentStream contentStream, TranscriptLine line) throws IOException {
        int indent;
        if (line.hasLineNumber()) {
            indent = line.fullText().trim().split("\\s")[0].length() + 1;
            draw(contentStream, indent, line.fullText().trim());
        }
        else {
            // Shift lines to the left to reduce indent size
            indent = 11;
            draw(contentStream, indent, line.fullText());

            // Fix spacing on first page. Spacing info is lost without line numbers marking blank lines.
            if (line.textTrimmed().equals("NEW YORK STATE SENATE"))
                contentStream.moveTextPositionByAmount(0, -(fontSize * 4));

            if (line.textTrimmed().contains("STENOGRAPHIC RECORD"))
                contentStream.moveTextPositionByAmount(0, -(fontSize * 5));

            if (line.isTime())
                contentStream.moveTextPositionByAmount(0, -(fontSize * 3));

            if (line.isSession())
                contentStream.moveTextPositionByAmount(0, -(fontSize * 6));
        }
    }

    private static void draw(PDPageContentStream contentStream, int indent, String text) throws IOException {
        float offset = left - indent * fontWidth;
        contentStream.moveTextPositionByAmount(offset, -fontSize);
        contentStream.drawString(text);
        contentStream.moveTextPositionByAmount(-offset, -fontSize);
    }

    private static ArrayList<List<String>> parsePages(String transcriptText) {
        List<String> page = new ArrayList<String>();
        ArrayList<List<String>> pages = new ArrayList<List<String>>();

        String[] line = transcriptText.split("\n");
        TranscriptLine nextLine;
        for (int i = 0; i < line.length; i++) {
            page.add(line[i]);

            // Ignore the first transcript number.
            if (i > 10) {
                if (i + 1 < line.length) {
                    nextLine = new TranscriptLine(line[i + 1]);

                    if (nextLine.isTranscriptNumber()) {
                        pages.add(page);
                        page = new ArrayList<String>();
                    }
                }
            }

            // add the last page.
            if (i == line.length - 1) {
                pages.add(page);
            }
        }
        return pages;
    }
}
