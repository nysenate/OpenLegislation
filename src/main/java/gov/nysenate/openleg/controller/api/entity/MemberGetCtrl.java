package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
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
    @Autowired private MemberService memberData;
    @Autowired private MemberSearchService memberSearch;

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
    public BaseResponse getMembersByYear(@RequestParam(defaultValue = "shortName:asc") String sort,
                                         @RequestParam(defaultValue = "false") boolean full,
                                         WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        SearchResults<SessionMember> results = memberSearch.searchMembers("*", sort, limOff);
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
    @RequestMapping(value = "/{sessionYear:\\d+}")
    public BaseResponse getMembersByYear(@PathVariable int sessionYear,
                                         @RequestParam(defaultValue = "shortName:asc") String sort,
                                         @RequestParam(defaultValue = "false") boolean full,
                                         WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        SearchResults<SessionMember> results = memberSearch.searchMembers(SessionYear.of(sessionYear), sort, limOff);
        return getMemberResponse(full, limOff, results);
    }

    /**
     * Member Listing API
     * ------------------
     *
     * Retrieve information for a member from a session year: (GET) /api/3/members/{sessionYear}/{id}
     * Request Parameters : full - If true, the full member view will be returned.
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{id:\\d+}")
    public BaseResponse getMembersByYear(@PathVariable int id,
                                         @PathVariable int sessionYear,
                                         @RequestParam(defaultValue = "true") boolean full,
                                         WebRequest request) throws SearchException, MemberNotFoundEx {
        return new ViewObjectResponse<>(
                (full) ? new MemberView(memberData.getMemberById(id, SessionYear.of(sessionYear)))
                        : new SimpleMemberView(memberData.getMemberById(id, SessionYear.of(sessionYear)))
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
    @RequestMapping(value = "/{sessionYear}/{chamber:\\D*}")
    public BaseResponse getMembersByYear(@PathVariable int sessionYear,
                                         @PathVariable String chamber,
                                         @RequestParam(defaultValue = "shortName:asc") String sort,
                                         @RequestParam(defaultValue = "false") boolean full,
                                         WebRequest request) throws SearchException, MemberNotFoundEx {
        LimitOffset limOff = getLimitOffset(request, 50);
        SearchResults<SessionMember> results = memberSearch.searchMembers(SessionYear.of(sessionYear), Chamber.getValue(chamber), sort, limOff);
        return getMemberResponse(full, limOff, results);
    }

    private BaseResponse getMemberResponse(boolean full, LimitOffset limOff, SearchResults<SessionMember> results) throws MemberNotFoundEx {
        List<ViewObject> memberList;
            memberList = results.getRawResults().stream()
                    .map(member -> memberData.getMemberById(member.getMemberId(), member.getSessionYear()))
                    .map(member -> full ? new MemberView(member) : new SimpleMemberView(member))
                    .collect(Collectors.toList());

        return ListViewResponse.of(memberList, results.getTotalResults(), limOff);
    }

    @ExceptionHandler(MemberNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ErrorResponse handleMemberNotFoundEx(MemberNotFoundEx ex) {
        return new ErrorResponse(ErrorCode.MEMBER_NOT_FOUND);
    }

}
