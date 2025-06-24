package gov.nysenate.openleg.api.config;

import gov.nysenate.openleg.api.BaseCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                  <url>
                    <loc>%s</loc>
                  </url>
                </urlset>
                """.formatted(domainUrl);
    }
}
