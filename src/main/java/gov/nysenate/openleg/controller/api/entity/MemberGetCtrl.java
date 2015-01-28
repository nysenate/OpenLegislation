package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.service.entity.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/members", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class MemberGetCtrl extends BaseCtrl
{

    @Autowired private MemberService memberData;

    /**
     * Member Listing API
     * ------------------
     *
     * Retrieve all members for a session year: (GET) /api/3/members/{sessionYear}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a member response.
     *                      full - If true, the full member view will be returned.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     */
    @RequestMapping(value = "/{sessionYear}")
    public BaseResponse getMembersByYear(@PathVariable int year,
                                         @RequestParam(defaultValue = "fullName:desc") String sort,
                                         @RequestParam(defaultValue = "false") boolean full,
                                         WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 50);

        return null;
    }

}
