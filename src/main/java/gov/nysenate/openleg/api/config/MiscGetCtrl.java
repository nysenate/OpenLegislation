package gov.nysenate.openleg.api.config;

import gov.nysenate.openleg.api.BaseCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/", method = RequestMethod.GET)
public class MiscGetCtrl extends BaseCtrl {
    private final String domainUrl;

    @Autowired
    public MiscGetCtrl(@Value("${domain.url}") String domainUrl) {
        this.domainUrl = domainUrl;
    }

    @RequestMapping(value = "sitemap.xml", produces = "application/xml")
    public String sitemap() {
        var strBuilder = new StringBuilder();
        for (String filePrefix : List.of("index", "bills", "calendars", "agendas", "committees", "laws",
                "transcripts_floor", "transcripts_ph", "members", "agg_updates", "search_api")) {
            strBuilder.append("""
                    <url>
                        <loc>%s/static/docs/%s.html</loc>
                    </url>""".formatted(domainUrl, filePrefix));
        }
        return """
            <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    %s
                </urlset>
            """.formatted(strBuilder);
    }
}
