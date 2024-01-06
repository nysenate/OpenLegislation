package gov.nysenate.openleg.api.processor;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.processor.view.SourceFileView;
import gov.nysenate.openleg.api.processor.view.SourceIdView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.SourceFile;
import gov.nysenate.openleg.processors.sourcefile.SourceFileRefDao;
import gov.nysenate.openleg.processors.sourcefile.sobi.LegDataFragmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;

/**
 * Source Retrieval APIs
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/sources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class SourceGetCtrl extends BaseCtrl {
    private final SourceFileRefDao sourceFileDao;
    private final LegDataFragmentDao legDataFragmentDao;

    @Autowired
    public SourceGetCtrl(SourceFileRefDao sourceFileDao, LegDataFragmentDao legDataFragmentDao) {
        this.sourceFileDao = sourceFileDao;
        this.legDataFragmentDao = legDataFragmentDao;
    }

    /**
     * SOBI File API
     * -------------
     * <p>
     * Retrieve a list of sobi files that were published between the given date range.
     * Usage: (GET) /api/3/sources/{from datetime}/{to datetime}
     * <p>
     * Params: order (string) - Order the results
     * limit, offset (int) - Pagination
     * <p>
     * Expected Output: List of SourceViewIds
     */
    @RequestMapping("/{from}/{to:.+}")
    public BaseResponse getSourcesDuring(@PathVariable String from, @PathVariable String to, WebRequest request) {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        SortOrder order = getSortOrder(request, SortOrder.ASC);
        LimitOffset limOff = getLimitOffset(request, 10);
        Range<LocalDateTime> dateTimeRange = getClosedOpenRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<SourceFile> sourceFiles = sourceFileDao.getSourceFilesDuring(dateTimeRange, order, limOff);
        return ListViewResponse.of(
                sourceFiles.results().stream()
                        .map(sourceFile -> new SourceIdView(sourceFile.getSourceType().name(),
                                sourceFile.getFileName(), sourceFile.getPublishedDateTime()))
                        .toList(), sourceFiles.total(), limOff);
    }

    /**
     * SOBI Fragment API
     * -----------------
     * <p>
     * Retrieve source fragments given a sobi file name.
     * Usage: (GET) /api/3/sources/{sobiFileName}
     * <p>
     * Expected Output: List of SourceFileView containing the fragments
     */
    @RequestMapping("/{sourceFileName:.+}")
    public BaseResponse getSourceFile(@PathVariable String sourceFileName, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 10);
        List<SourceFileView> fragList = legDataFragmentDao.getLegDataFragments(sourceFileName, SortOrder.ASC).stream()
                .map(sf -> new SourceFileView(sf.getType().name(), sf.getFragmentId(),
                        sf.getPublishedDateTime(), sf.getText()))
                .toList();
        return ListViewResponse.of(fragList, fragList.size(), limOff);
    }

    /**
     * SOBI Fragment API
     * -----------------
     * <p>
     * Retrieve a sobi fragment given a fragment id.
     * Usage: (GET) /api/3/sources/fragment/{fragmentId}
     * <p>
     * Expected Output: SourceFileView
     */
    @RequestMapping("/fragment/{fragmentId:.+}")
    public BaseResponse getSobiFragmentSource(@PathVariable String fragmentId) {
        LegDataFragment fragment = legDataFragmentDao.getLegDataFragment(fragmentId);
        return new ViewObjectResponse<>(
                new SourceFileView(fragment.getType().name(), fragment.getFragmentId(),
                        fragment.getPublishedDateTime(), fragment.getText()));
    }

    /**
     * --- Exception Handlers ---
     */

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyResultException(EmptyResultDataAccessException ex) {
        return new ErrorResponse(ErrorCode.SOURCE_FILE_NOT_FOUND);
    }

}
