package gov.nysenate.openleg.processors.scripts.member;

import gov.nysenate.openleg.common.script.BaseScript;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.common.util.MemberScraperUtils;
import gov.nysenate.openleg.common.util.RandomUtils;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class MemberScraperCLI extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(MemberScraperCLI.class);

    @Override
    protected void execute(CommandLine opts) throws Exception {
        List<SessionMember> assemblyMembers = MemberScraperUtils.getAssemblyMembers();
        assemblyMembers.forEach(sm -> {
            try {
                InputStream in = new UrlResource(sm.getMember().getPerson().getImgName()).getInputStream();
                FileIOUtils.writeToFile(in, "/tmp/assembly/" + RandomUtils.getRandomString(10) + ".jpg");
            } catch (IOException e) {
                logger.error("Failed to ", e);
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
