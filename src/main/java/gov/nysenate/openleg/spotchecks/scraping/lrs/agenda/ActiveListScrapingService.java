package gov.nysenate.openleg.spotchecks.scraping.lrs.agenda;

import gov.nysenate.openleg.spotchecks.model.ActiveListHTMLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by kyle on 12/8/14.
 */
@Service
public class ActiveListScrapingService {
    private static final String directoryName = "/data/openleg/scraped/CALENDAR/";
    private final SqlActiveListReferenceDAO dao;

    @Autowired
    public ActiveListScrapingService(SqlActiveListReferenceDAO dao) {
        this.dao = dao;
    }

    public void main() throws Exception {
        File[] fileList = new File(directoryName).listFiles();
        if (fileList == null) {
            return;
        }
        for (File file : fileList) {
            // just for Active Lists
            if (file.toString().contains("active_list")) {
                dao.addCalendarReference(ActiveListHTMLParser.getSpotcheckReference(file));
            }
        }
    }
}
