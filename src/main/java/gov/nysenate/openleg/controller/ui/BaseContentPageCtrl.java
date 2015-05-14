package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.entity.member.search.MemberSearchService;
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

    protected static List<Member> senatorsList = null;
    protected static List<Member> assemblyMemList = null;

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
                SearchResults<Member> senResults = memberSearchService.searchMembers(SessionYear.current(), Chamber.SENATE,
                        sort, LimitOffset.THOUSAND);
                senatorsList = senResults.getResults().stream().map(r -> {
                    try {
                        return memberData.getMemberById(r.getResult().getMemberId(), r.getResult().getSessionYear());
                    } catch (MemberNotFoundEx memberNotFoundEx) {
                        logger.error("Failed to fetch senator!", memberNotFoundEx);
                    }
                    return null;
                }).collect(Collectors.toList());

                SearchResults<Member> assemResults = memberSearchService.searchMembers(SessionYear.current(), Chamber.ASSEMBLY,
                        sort, LimitOffset.THOUSAND);
                assemblyMemList = assemResults.getResults().stream().map(r -> {
                    try {
                        return memberData.getMemberById(r.getResult().getMemberId(), r.getResult().getSessionYear());
                    } catch (MemberNotFoundEx memberNotFoundEx) {
                        logger.error("Failed to fetch assembly member!", memberNotFoundEx);
                    }
                    return null;
                }).collect(Collectors.toList());
            }
            catch (SearchException e) {
                logger.error("Failed to fetch members!", e);
            }
        }
    }
}