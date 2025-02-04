package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MemberProcessor extends AbstractDataProcessor {
    private final MemberDao memberDao;

    @Autowired
    public MemberProcessor(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void process(Path path) throws IOException, SAXException {
        Document document = xmlHelper.parse(Files.readString(path));
        // TODO
    }
}
