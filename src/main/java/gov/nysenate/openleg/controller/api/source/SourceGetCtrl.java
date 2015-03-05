package gov.nysenate.openleg.controller.api.source;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.source.SourceFileView;
import gov.nysenate.openleg.client.view.source.SourceIdView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SobiDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Source Retrieval APIs
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/sources", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class SourceGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SourceGetCtrl.class);

    @Autowired private SobiDao sobiDao;

    /**
     * SOBI File API
     * -------------
     *
     * Retrieve a list of sobi files that were published between the given date range.
     * Usage: (GET) /api/3/sources/sobi/{from datetime}/{to datetime}
     *
     * Params: order (string) - Order the results
     *         limit, offset (int) - Pagination
     *
     * Expected Output: List of SourceViewIds
     */
    @RequestMapping("/sobi/{from}/{to:.+}")
    public BaseResponse getSobiSourcesDuring(@PathVariable String from, @PathVariable String to, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        SortOrder order = getSortOrder(request, SortOrder.ASC);
        LimitOffset limOff = getLimitOffset(request, 10);
        Range<LocalDateTime> dateTimeRange = getClosedOpenRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<SobiFile> sobiFiles = sobiDao.getSobiFilesDuring(dateTimeRange, order, limOff);
        return ListViewResponse.of(
            sobiFiles.getResults().stream()
                .map(sobiFile -> new SourceIdView("SOBI File", sobiFile.getFileName(), sobiFile.getPublishedDateTime()))
                .collect(Collectors.toList()), sobiFiles.getTotal(), limOff);
    }

    /**
     * SOBI Fragment API
     * -----------------
     *
     * Retrieve sobi fragments given a sobi file name.
     * Usage: (GET) /api/3/sources/sobi/{sobiFileName}
     *
     * Expected Output: List of SourceFileView containing the fragments
     */
    @RequestMapping("/sobi/{sobiFileName:.+}")
    public BaseResponse getSobiSource(@PathVariable String sobiFileName) {
        SobiFile sobiFile = sobiDao.getSobiFile(sobiFileName);
        List<SourceFileView> fragList = sobiDao.getSobiFragments(sobiFile, SortOrder.ASC).stream()
            .map(sf -> new SourceFileView(sf.getType().name(), sf.getFragmentId(),
                                          sf.getPublishedDateTime(), sf.getText()))
            .collect(Collectors.toList());
        return ListViewResponse.of(fragList, fragList.size(), LimitOffset.ALL);
    }

    /**
     * SOBI Fragment API
     * -----------------
     *
     * Retrieve a sobi fragment given a fragment id.
     * Usage: (GET) /api/3/sources/sobi/fragment/{fragmentId}
     *
     * Expected Output: SourceFileView
     */
    @RequestMapping("/sobi/fragment/{fragmentId:.+}")
    public BaseResponse getSobiFragmentSource(@PathVariable String fragmentId) {
        SobiFragment fragment = sobiDao.getSobiFragment(fragmentId);
        return new ViewObjectResponse<>(
            new SourceFileView(fragment.getType().name(), fragment.getFragmentId(),
                               fragment.getPublishedDateTime(), fragment.getText()));
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyResultException(EmptyResultDataAccessException ex) {
        return new ErrorResponse(ErrorCode.SOURCE_FILE_NOT_FOUND);
    }

}
