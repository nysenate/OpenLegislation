package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.entity.member.search.MemberSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/members", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class MemberGetCtrl extends BaseCtrl
{
    protected final MemberService memberData;
    protected final MemberSearchService memberSearch;

    @Autowired
    public MemberGetCtrl(MemberService memberData, MemberSearchService memberSearch) {
        this.memberData = memberData;
        this.memberSearch = memberSearch;
    }

    /**
     * Member Listing API
     * ------------------
     *
     * Get all members.
     * Request Parameters : sort - Lucene syntax for sorting by any field of a member response.
     *                      full - If true, the full member view will be returned.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
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
     *                      full - If true, the full member view will be returned.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
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
                                         @RequestParam(defaultValue = "true") boolean full,
                                         WebRequest request) throws MemberNotFoundEx {
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
     *                      full - If true, the full member view will be returned.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
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

    private BaseResponse getMemberResponse(boolean full, LimitOffset limOff, SearchResults<Integer> results) throws MemberNotFoundEx {
        List<ViewObject> memberList = results.getRawResults().stream()
                .map(memberData::getFullMemberById)
                .map(member -> full ? new FullMemberView(member) : new SessionMemberView(member.getLatestSessionMember().get()))
                .collect(Collectors.toList());
        return ListViewResponse.of(memberList, results.getTotalResults(), limOff);
    }

    @ExceptionHandler(MemberNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ErrorResponse handleMemberNotFoundEx(MemberNotFoundEx ex) {
        return new ErrorResponse(ErrorCode.MEMBER_NOT_FOUND);
    }

}
