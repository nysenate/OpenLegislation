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

        for (String[] pageLines : pages) {
            PDPage page = new PDPage(PDPage.PAGE_SIZE_LETTER);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.beginText();
            contentStream.setFont(font, 11);
            contentStream.moveTextPositionByAmount(75, 725);
            for (String line : pageLines) {
                contentStream.moveTextPositionByAmount(0f, -12f);
                contentStream.drawString(line);
            }
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
