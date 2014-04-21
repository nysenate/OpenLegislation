package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Transcript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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
        ArrayList<String[]> pages = pagify(transcript.getTranscriptText());

        Float bot = 90f;
        Float right = 575f;
        Float top = 710f;
        Float left = 105f;
        Float fontSize = 12f;
        Float fontWidth = 7f;

        for (String[] pageLines : pages) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.drawLine(left, top, left, bot);
            contentStream.drawLine(left, top, right, top);
            contentStream.drawLine(left, bot, right, bot);
            contentStream.drawLine(right, top, right, bot);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            boolean firstLine = true;
            for (String line : pageLines) {
                line = line.trim();
                String[] parts = line.split("\\s");
                if (parts.length > 0 && parts[0].matches("[0-9]+")) {
                    if (firstLine) {
                        float offset = right-(parts[0].length()+1)*fontWidth;
                        contentStream.moveTextPositionByAmount(offset, top+fontWidth);
                        contentStream.drawString(parts[0]);
                        contentStream.moveTextPositionByAmount(-offset, -fontSize*2);
                        firstLine = false;
                    }
                    else {
                        float offset = left-(parts[0].length()+1)*fontWidth;
                        contentStream.moveTextPositionByAmount(offset, -fontSize);
                        contentStream.drawString(line);
                        contentStream.moveTextPositionByAmount(-offset, -fontSize);
                    }
                }
            }

            String stenographer;
            if (transcript.getTimeStamp().getTime() >= KIRKLAND_START_TIME) {
                stenographer = "Kirkland Reporting Service";
            }
            else {
                stenographer = "Candyco Transcription Service, Inc.";
            }
            contentStream.moveTextPositionByAmount(left+ (right-left-stenographer.length()*fontWidth)/2, -fontSize*2);
            contentStream.drawString(stenographer);

            contentStream.endText();
            contentStream.close();
            doc.addPage(page);
        }
        doc.save(out);
        doc.close();
    }

    public static ArrayList<String[]> pagify(String text)
    {
        ArrayList<String[]> pages = new ArrayList<String[]>();
        for (String pageText : text.split((char)12+"")) {
            String[] lines = pageText.split("\n");
            if (lines.length > 0) {
                pages.add(lines);
            }
        }
        return pages;
    }
}
