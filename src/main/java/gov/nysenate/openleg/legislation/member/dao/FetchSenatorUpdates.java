package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.services.NYSenateClientService;
import gov.nysenate.services.NYSenateJSONClient;
import gov.nysenate.services.model.Senator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FetchSenatorUpdates {
    private final NYSenateClientService senateClientService;
    private final MemberService memberService;
    private final String sourceCodeDir;
    private final String memberImgDir;

    @Autowired
    public FetchSenatorUpdates(MemberService memberService,
                                   @Value("${source.code.directory:/tmp}") String sourceCodeDir) throws IOException {
        this.senateClientService = new NYSenateJSONClient();
        this.memberService = memberService;
        this.sourceCodeDir = sourceCodeDir.endsWith("/") ? sourceCodeDir : sourceCodeDir + "/";
        this.memberImgDir = this.sourceCodeDir + "src/main/webapp/static/img/business_assets/members/mini/";
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void applyUpdates() throws IOException {

        List<FullMember> updatedMembers = new ArrayList<>();
        Map<Integer, FullMember> membersMissingImages = memberService.getAllFullMembers().stream()
                .filter(m -> m.getPerson().imgName().equals("no_image.jpg"))
                .collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
        Map<Integer, Senator> senators = senateClientService.getSenators().stream()
                .filter(senator -> membersMissingImages.containsKey(senator.getOpenLegId()))
                .collect(Collectors.toMap(Senator::getOpenLegId, Function.identity()));

        // Save member images locally.
        for (var entry : senators.entrySet()) {
            // Get image and write to file.
            InputStream in = new UrlResource(entry.getValue().getImageUrl()).getInputStream();
            FullMember updatedFullMember =  membersMissingImages.get(entry.getKey());
            FileIOUtils.writeToFile(in, memberImgDir + membersMissingImages.get(entry.getKey())
                    .getPerson().getSuggestedImageFileName());
            updatedMembers.add(updatedFullMember);
        }

        // Create a database migration to update members img names.
        createMigration(updatedMembers);
    }

    // TODO: rewrite to use member XML updates.
    private void createMigration(List<FullMember> members) throws IOException {
        String migrationDir = this.sourceCodeDir + "src/main/resources/sql/migrations/";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd.hhmm");
        String migrationName = "V" + now.format(formatter) + "__add_member_images.sql";

        StringBuilder builder = new StringBuilder();
        for (FullMember m : members) {
            builder.append("UPDATE public.person SET img_name = ")
                    .append("'%s' ".formatted(m.getPerson().getSuggestedImageFileName()))
                    .append("WHERE id = %d;%n%n".formatted(m.getPerson().personId()));
        }

        File file = new File(migrationDir + migrationName);
        FileIOUtils.writeStringToFile(file, builder.toString());
    }
}
