package gov.nysenate.openleg.processors.scripts.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.common.script.BaseScript;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.common.util.HttpUtils;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import org.apache.commons.cli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A script to assist with downloading missing member headshot photos.
 *
 * This script will download missing headshot photos for senators (does not currently handle assembly members).
 * It will also create an sql migration to update the database with the image names.
 * Both the images and sql migration need to be reviewed and committed to the repository.
 *
 * Run this script in intellij by running the main method. You will have to edit the configuration
 * and add a VM option of -Dspring.profiles.active=dev
 *
 * Also, be sure the property 'source.code.directory' is pointed at the location of the OpenLegislation
 * repository on your computer.
 *
 * TODO update this to use our java utils package instead of hitting the API (https://github.com/nysenate/nysenate-java-utils)
 */
@Component
public class FetchMemberImagesScript extends BaseScript {

    private MemberService memberService;
    private ObjectMapper objectMapper;
    private String sourceCodeDir;
    private String memberImgDir;

    @Autowired
    public FetchMemberImagesScript(MemberService memberService, ObjectMapper objectMapper,
                                   @Value("${source.code.directory:/tmp}") String sourceCodeDir) {
        this.memberService = memberService;
        this.objectMapper = objectMapper;
        this.sourceCodeDir = sourceCodeDir.endsWith("/") ? sourceCodeDir : sourceCodeDir + "/";
        this.memberImgDir = this.sourceCodeDir + "src/main/webapp/static/img/business_assets/members/mini/";
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = init();
        FetchMemberImagesScript fetchMemberImagesScript = ctx.getBean(FetchMemberImagesScript.class);
        CommandLine cmd = getCommandLine(fetchMemberImagesScript.getOptions(), args);
        fetchMemberImagesScript.execute(cmd);
        shutdown(ctx);
        System.exit(0);
    }

    @Override
    public void execute(CommandLine opts) throws Exception {
        List<FullMember> updatedMembers = new ArrayList<>();
        List<FullMember> membersMissingImages = memberService.getAllFullMembers().stream()
                .filter(m -> m.getImgName() == null || m.getImgName().isEmpty() || m.getImgName().equals("no_image.jpg"))
                .collect(Collectors.toList());

        Map<Integer, MemberJsonFeedView> memberIdToJsonMember = getMembersFromJsonFeed();

        // Save member images locally.
        for (FullMember member : membersMissingImages) {
            if (memberIdToJsonMember.containsKey(member.getMemberId())) {
                saveImageFile(memberIdToJsonMember, member);
                updatedMembers.add(member);
            }
        }

        // Create a database migration to update members img names.
        createMigration(updatedMembers);
    }

    private void saveImageFile(Map<Integer, MemberJsonFeedView> memberIdToJsonMember, FullMember member) throws IOException {
        MemberJsonFeedView jsonFeedMember = memberIdToJsonMember.get(member.getMemberId());
        // Get image and write to file.
        InputStream in = new UrlResource(jsonFeedMember.getImg()).getInputStream();
        FileIOUtils.writeToFile(in, memberImgDir + member.getSuggestedImageFileName());
    }

    private void createMigration(List<FullMember> members) throws IOException {
        String migrationDir = this.sourceCodeDir + "src/main/resources/sql/migrations/";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd.hhmm");
        String migrationName = "V" + now.format(formatter) + "__add_member_images.sql";

        StringBuilder builder = new StringBuilder();
        for (FullMember m : members) {
            builder.append("UPDATE public.person SET img_name = ");
            builder.append("'");
            builder.append(m.getSuggestedImageFileName());
            builder.append("' ");
            builder.append("WHERE id = ");
            builder.append(m.getPersonId());
            builder.append(";");
            builder.append("\n\n");
        }

        File file = new File(migrationDir + migrationName);
        FileIOUtils.writeStringToFile(file, builder.toString());
    }

    private Map<Integer, MemberJsonFeedView> getMembersFromJsonFeed() throws IOException {
        HttpUtils httpUtils = new HttpUtils();
        String senatorJsonFeed = "https://www.nysenate.gov/senators.json";
        String json = httpUtils.urlToString(senatorJsonFeed);
        MemberJsonFeedView[] jsonMembers = objectMapper.readValue(json, MemberJsonFeedView[].class);
        return Arrays.stream(jsonMembers)
                .collect(Collectors.toMap(MemberJsonFeedView::getOpen_leg_id, Function.identity()));
    }
}
