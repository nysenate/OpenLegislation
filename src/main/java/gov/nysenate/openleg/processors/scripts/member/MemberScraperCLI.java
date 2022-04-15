package gov.nysenate.openleg.processors.scripts.member;

import gov.nysenate.openleg.common.script.BaseScript;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.apache.commons.cli.CommandLine;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Quick and dirty member scraping. This scraper will break with site redesigns so always test first.
 */
@Service
public class MemberScraperCLI extends BaseScript {
    private static final Logger logger = LoggerFactory.getLogger(MemberScraperCLI.class);
    private static final String ASSEMBLY_MEMBER_URL = "https://assembly.state.ny.us/mem/";

    @Override
    protected void execute(CommandLine opts) throws Exception {
        Elements picElements = Jsoup.connect(ASSEMBLY_MEMBER_URL).get().select(".mem-pic a img");
        List<String> imgNames = picElements.stream().map(i -> i.attr("src")).toList();
        imgNames.forEach(imgName -> {
            try {
                InputStream in = new UrlResource(imgName).getInputStream();
                FileIOUtils.writeToFile(in, "/tmp/assembly/" + imgName);
            } catch (IOException e) {
                logger.error("Error in " + MemberScraperCLI.class.getName(), e);
            }
        });
    }

    public static void main(String[] args) throws Exception {
        SCRIPT_NAME = MemberScraperCLI.class.getName();
        AnnotationConfigApplicationContext ctx = init();
        MemberScraperCLI memberScraperCLI = ctx.getBean(MemberScraperCLI.class);
        CommandLine cmd = getCommandLine(memberScraperCLI.getOptions(), args);
        memberScraperCLI.execute(cmd);
        shutdown(ctx);
    }
}
