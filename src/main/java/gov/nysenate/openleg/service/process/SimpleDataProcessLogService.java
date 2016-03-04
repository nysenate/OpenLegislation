package gov.nysenate.openleg.service.process;

import com.google.common.collect.Range;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.process.DataProcessLogDao;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessRunInfo;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class SimpleDataProcessLogService implements DataProcessLogService
{
    @Autowired private Environment env;
    @Autowired private DataProcessLogDao processLogDao;

    /** {@inheritDoc} */
    @Override
    public Optional<DataProcessRun> getRun(int processId) {
        try {
            return Optional.of(processLogDao.getRun(processId));
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<DataProcessRunInfo> getRunInfo(int processId) {
        Optional<DataProcessRun> run = getRun(processId);
        if (run.isPresent()) {
            return Optional.of(getRunInfoFromRun(run.get()));
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<DataProcessRunInfo> getRunInfos(Range<LocalDateTime> dateTimeRange, LimitOffset limOff,
                                                         boolean showActivityOnly) {
        PaginatedList<DataProcessRun> runs = processLogDao.getRuns(dateTimeRange, showActivityOnly, SortOrder.DESC, limOff);
        List<DataProcessRunInfo> runInfos = runs.getResults().stream().map(this::getRunInfoFromRun).collect(toList());
        return new PaginatedList<>(runs.getTotal(), runs.getLimOff(), runInfos);
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<DataProcessUnit> getUnits(int processId, LimitOffset limOff) {
        return processLogDao.getUnits(processId, SortOrder.DESC, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public DataProcessRun startNewRun(LocalDateTime startDateTime, String invoker) {
        DataProcessRun run = new DataProcessRun(0, startDateTime, invoker);
        if (env.isProcessLoggingEnabled()) {
            processLogDao.insertRun(run);
        }
        return run;
    }

    /** {@inheritDoc} */
    @Override
    public void addUnit(int processId, DataProcessUnit unit) {
        if (env.isProcessLoggingEnabled()) {
            processLogDao.insertUnit(processId, unit);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void finishRun(DataProcessRun run) {
        if (env.isProcessLoggingEnabled()) {
            run.setEndDateTime(LocalDateTime.now());
            processLogDao.updateRun(run);
        }
    }

    private DataProcessRunInfo getRunInfoFromRun(DataProcessRun run) {
        DataProcessRunInfo runInfo = new DataProcessRunInfo(run);
        List<DataProcessUnit> firstAndLastUnits = processLogDao.getFirstAndLastUnits(run.getProcessId());
        if (!firstAndLastUnits.isEmpty()) {
            runInfo.setFirstProcessed(Optional.of(firstAndLastUnits.get(0)));
            if (firstAndLastUnits.size() > 1) {
                runInfo.setLastProcessed(Optional.of(firstAndLastUnits.get(1)));
            }
        }
        return runInfo;
    }
}