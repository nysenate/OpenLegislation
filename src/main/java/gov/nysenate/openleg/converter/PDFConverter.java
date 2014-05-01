package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Transcript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    // Line feeds started on Feb 7, 2008
    protected static long LINEFEED_START_TIME = 1202360400000L;

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
        if (transcript.getTimeStamp().getTime() > LINEFEED_START_TIME) {
            pages = pagifyWithLineFeeds(transcript.getTranscriptText());
        }
        else {
            pages = pagifyByString(transcript.getTranscriptText(), "(518) 371-8910");
        }

        Float bot = 90f;
        Float right = 575f;
        Float top = 710f;
        Float left = 105f;
        Float fontSize = 12f;
        Float fontWidth = 7f;

        for (List<String> pageLines : pages) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.drawLine(left, top, left, bot);
            contentStream.drawLine(left, top, right, top);
            contentStream.drawLine(left, bot, right, bot);
            contentStream.drawLine(right, top, right, bot);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            int lineCount = 0;
            for (String line : pageLines) {
                line = line.trim();
                String[] parts = line.split("\\s");
                if (parts.length > 0 && parts[0].matches("[0-9]+")) {
                    if (lineCount == 0) {
                        float offset = right-(parts[0].length()+1)*fontWidth;
                        contentStream.moveTextPositionByAmount(offset, top+fontWidth);
                        contentStream.drawString(parts[0]);
                        contentStream.moveTextPositionByAmount(-offset, -fontSize*2);
                    }
                    else {
                        float offset = left-(parts[0].length()+1)*fontWidth;
                        contentStream.moveTextPositionByAmount(offset, -fontSize);
                        contentStream.drawString(line);
                        contentStream.moveTextPositionByAmount(-offset, -fontSize);
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

    public static ArrayList<List<String>> pagifyByString(String text, String separator)
    {
        List<String> page = new ArrayList<String>();
        ArrayList<List<String>> pages = new ArrayList<List<String>>();
        for (String line : text.split("\n")) {
            page.add(line);
            if (line.trim().equals(separator)) {
                // The separator is always the last line on the old page
                pages.add(page);
                page = new ArrayList<String>();
            }
        }
        return pages;
    }
    public static ArrayList<List<String>> pagifyWithLineFeeds(String text)
    {
        ArrayList<List<String>> pages = new ArrayList<List<String>>();
        for (String pageText : text.split((char)12+"")) {
            String[] lines = pageText.split("\n");
            if (lines.length > 0) {
                pages.add(Arrays.asList(lines));
            }
        }
        return pages;
    }
}
