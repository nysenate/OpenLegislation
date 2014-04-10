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

        for (String[] pageLines : pages) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.drawLine(left, top, left, bot);
            contentStream.drawLine(left, top, right, top);
            contentStream.drawLine(left, bot, right, bot);
            contentStream.drawLine(right, top, right, bot);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.moveTextPositionByAmount(85, 730);
            boolean firstLine = true;
            for (String line : pageLines) {
                if (line.length() > 0 && firstLine == false) {
                    line = line.substring(0, 2)+"   "+line.substring(2);
                }
                contentStream.moveTextPositionByAmount(0f, -fontSize);
                contentStream.drawString(line);
                if (firstLine) {
                    contentStream.moveTextPositionByAmount(0f, -fontSize);
                    contentStream.drawString("");
                    firstLine = false;
                }
            }

            String stenographer = "Kirkland Reporting Service";
            contentStream.moveTextPositionByAmount(0f, -12f);
            contentStream.moveTextPositionByAmount(0f, -12f);
            contentStream.moveTextPositionByAmount(0f, -12f);
            contentStream.moveTextPositionByAmount((right-left-stenographer.length()*5)/2, 0f);
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
