package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.entity.member.search.MemberSearchService;
import gov.nysenate.openleg.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common base class for all controllers that are responsible for rending content pages.
 */
public abstract class BaseContentPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BaseContentPageCtrl.class);

    @Autowired private MemberService memberData;
    @Autowired private MemberSearchService memberSearchService;

    protected static List<SessionMember> senatorsList = null;
    protected static List<SessionMember> assemblyMemList = null;

    protected void baseInit() {
        initializeMembers();
    }

    /**
     * Adds common content request attributes. This is needed for things like senator drop downs
     * which would otherwise require additional API calls.
     *
     * @param request HttpServletRequest
     */
    protected void addContentAttributesToRequest(HttpServletRequest request) {
        request.setAttribute("senatorList", senatorsList);
        request.setAttribute("assemblyMemList", assemblyMemList);
    }

    /**
     * Initializes the members list for use in drop downs in non-member centric pages. This data could be retrieved
     * by an API call but it seems overkill to have to make a separate API call just for this listing which is fairly
     * static..
     */
    private void initializeMembers() {
        if (senatorsList == null || assemblyMemList == null) {
            try {
                String sort = "shortName:asc";
                SearchResults<SessionMember> senResults = memberSearchService.searchMembers(SessionYear.current(), Chamber.SENATE,
                        sort, LimitOffset.THOUSAND);
                senatorsList = fetchMembers(senResults);

                SearchResults<SessionMember> assemResults = memberSearchService.searchMembers(SessionYear.current(), Chamber.ASSEMBLY,
                        sort, LimitOffset.THOUSAND);
                assemblyMemList = fetchMembers(assemResults);
            }
            catch (SearchException e) {
                logger.error("Failed to fetch members!", e);
            }
        }
    }

    private List<SessionMember> fetchMembers(SearchResults<SessionMember> results) {
        return results.getRawResults().stream().map(result -> {
            try {
                return memberData.getMemberById(result.getMemberId(), result.getSessionYear());
            } catch (MemberNotFoundEx memberNotFoundEx) {
                logger.error("Failed to fetch senator!", memberNotFoundEx);
            } catch (IllegalArgumentException ex) {
                logger.error("bad search result:\n{}", OutputUtils.toJson(result));
            }
            return null;
        }).collect(Collectors.toList());
    }
}