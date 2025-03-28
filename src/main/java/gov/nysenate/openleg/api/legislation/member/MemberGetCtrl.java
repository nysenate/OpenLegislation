package gov.nysenate.openleg.api.legislation.member;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.api.legislation.member.view.SessionMemberView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.MemberProcessor;
import gov.nysenate.openleg.processors.MemberType;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.member.MemberSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/members", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class MemberGetCtrl extends BaseCtrl {
    private static final Logger log = LoggerFactory.getLogger(MemberGetCtrl.class);
    private final MemberService memberData;
    private final MemberSearchService memberSearch;
    private final MemberProcessor memberProcessor;
    private final DataProcessor dataProcessor;
    private final String stagingDirectory;

    @Autowired
    public MemberGetCtrl(MemberService memberData, MemberSearchService memberSearch, MemberProcessor memberProcessor,
                         DataProcessor dataProcessor, @Value("${member.staging}") String sourceCodeDir) {
        this.memberData = memberData;
        this.memberSearch = memberSearch;
        this.memberProcessor = memberProcessor;
        this.dataProcessor = dataProcessor;
        this.stagingDirectory = sourceCodeDir;
    }

    /**
     * Member Listing API
     * ------------------
     * <p>
     * Get all members.
     * Request Parameters : sort - Lucene syntax for sorting by any field of a member response.
     * full - If true, the full member view will be returned.
     * limit - Limit the number of results
     * offset - Start results from an offset.
     */
    @RequestMapping(value = "")
    public BaseResponse getAllMembers(@RequestParam(defaultValue = "shortName:asc") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        SearchResults<Integer> results = memberSearch.searchMembers("*", sort, limOff);
        return getMemberResponse(full, limOff, results);
    }

    /**
     * Member Listing API
     * ------------------
     *
     * Retrieve all members for a session year: (GET) /api/3/members/{sessionYear}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a member response.
     * full - If true, the full member view will be returned.
     * limit - Limit the number of results
     * offset - Start results from an offset.
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}")
    public BaseResponse getMembersByYear(@PathVariable int sessionYear,
                                         @RequestParam(defaultValue = "shortName:asc") String sort,
                                         @RequestParam(defaultValue = "false") boolean full,
                                         WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        SearchResults<Integer> results = memberSearch.searchMembers(SessionYear.of(sessionYear), sort, limOff);
        return getMemberResponse(full, limOff, results);
    }

    /**
     * Member Listing API
     * ------------------
     *
     * Retrieve information for a member from a session year: (GET) /api/3/members/{sessionYear}/{id}
     * Request Parameters : full - If true, the full member view will be returned.
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{memberId:\\d+}")
    public BaseResponse getMembersByYearAndId(@PathVariable int memberId,
                                              @PathVariable int sessionYear,
                                              @RequestParam(defaultValue = "true") boolean full)
            throws MemberNotFoundEx {
        return new ViewObjectResponse<>(
                (full) ? new FullMemberView(memberData.getFullMemberById(memberId))
                        : new SessionMemberView(memberData.getSessionMemberById(memberId, SessionYear.of(sessionYear)))
        );
    }

    /**
     * Member Listing API
     * ------------------
     *
     * Retrieve all members of a chamber for a session year: (GET) /api/3/members/{sessionYear}/{chamber}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a member response.
     * full - If true, the full member view will be returned.
     * limit - Limit the number of results
     * offset - Start results from an offset.
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamber:\\D+}")
    public BaseResponse getMembersByYearAndChamber(@PathVariable int sessionYear,
                                                   @PathVariable String chamber,
                                                   @RequestParam(defaultValue = "shortName:asc") String sort,
                                                   @RequestParam(defaultValue = "false") boolean full,
                                                   WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        Chamber chamberValue = getEnumParameter("chamber", chamber, Chamber.class);
        SearchResults<Integer> results =
                memberSearch.searchMembers(SessionYear.of(sessionYear), chamberValue, sort, limOff);
        return getMemberResponse(full, limOff, results);
    }

    @PutMapping(value = "/{memberTable}")
    public BaseResponse createMemberXml(@PathVariable String memberTable, @RequestParam("action") String action,
                                        @RequestBody Map<String, String> modelMap) {
        MemberChangeType changeType = MemberChangeType.valueOf(action);
        MemberType tableName = MemberType.valueOf(memberTable);
        StringBuilder xmlBuilder = switch (tableName) {
            case MEMBER -> memberProcessor.getMemberXmlBuilder(changeType, modelMap, tableName);
            case PERSON -> memberProcessor.getPersonXmlBuilder(changeType, modelMap, tableName);
            case SESSION -> memberProcessor.getSessionXmlBuilder(changeType, modelMap, tableName);
        };
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = now.format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss.SSSSSS");
        String time = now.format(timeFormatter);

        String filePath = stagingDirectory + "/" + date + "-" + time + "_member_1.xml";

        try {
            var xmlFile = new File(filePath);
            FileIOUtils.writeStringToFile(xmlFile, xmlBuilder.toString());
            dataProcessor.run("Create Member XML");
        } catch (Exception e) {
            return new ErrorResponse(ErrorCode.MEMBER_CHANGE_FAILURE);
        }

        return new SimpleResponse(true, String.format("Successfully Created the %sMemberXml file", action), "createMemberXml");
    }

    private BaseResponse getMemberResponse(boolean full, LimitOffset limOff, SearchResults<Integer> results) throws MemberNotFoundEx {
        List<ViewObject> memberList = results.getRawResults().stream()
                .map(memberData::getFullMemberById)
                .map(member -> full ? new FullMemberView(member) :
                        new SessionMemberView(member.getLatestSessionMember().get()))
                .toList();
        return ListViewResponse.of(memberList, results.totalResults(), limOff);
    }

    @ExceptionHandler(MemberNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ErrorResponse handleMemberNotFoundEx(MemberNotFoundEx ex) {
        return new ErrorResponse(ErrorCode.MEMBER_NOT_FOUND);
    }
}
