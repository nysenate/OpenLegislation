package gov.nysenate.openleg.processor.hearing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicHearingTextParser
{

    public String parse(List<List<String>> pages) {
        StringBuffer text = new StringBuffer();

        for (List<String> page : pages) {
            for (String line : page) {
                text.append(line);
                text.append("\n");
            }
        }

        return text.toString();
    }
}
