package gov.nysenate.openleg.spotchecks.sensite;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceEvent;
import gov.nysenate.openleg.spotchecks.sensite.bill.FsSenateSiteDao;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.shiro.event.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SenateSiteArchiveManager {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteArchiveManager.class);

    private EventBus eventBus;
    private OpenLegEnvironment environment;
    private final List<SpotCheckRefType> senateSiteRefTypes;
    private final int monthsToKeep;

    @Autowired
    public SenateSiteArchiveManager(EventBus eventBus, OpenLegEnvironment environment,
                                    @Value("${spotcheck.senatesite.archives.months.to.keep:6}") int monthsToKeep) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.environment = environment;
        this.monthsToKeep = monthsToKeep;

        this.senateSiteRefTypes = new ArrayList<>();
        this.senateSiteRefTypes.add(SpotCheckRefType.SENATE_SITE_AGENDA);
        this.senateSiteRefTypes.add(SpotCheckRefType.SENATE_SITE_BILLS);
        this.senateSiteRefTypes.add(SpotCheckRefType.SENATE_SITE_CALENDAR);
        this.senateSiteRefTypes.add(SpotCheckRefType.SENATE_SITE_LAW);
    }

    /**
     * Whenever a senate site spotcheck is run, this will review the archived files
     * and delete files more than 'monthsToKeep' months old.
     * @param event
     * @throws IOException
     */
    @Subscribe
    public synchronized void handleSpotcheckReferenceEvent(SpotCheckReferenceEvent event) throws IOException {
        if (senateSiteRefTypes.contains(event.getRefType())) {
            deleteOldArchives(event.getRefType());
        }
    }

    private void deleteOldArchives(SpotCheckRefType refType) throws IOException {
        File archiveDir = getArchiveBillDir(refType);
        Collection<File> archivedFiles = FileUtils.listFiles(archiveDir, TrueFileFilter.TRUE, null);
        Pattern fileDatePattern = Pattern.compile(refType.getRefName() + "_dump-\\d{4}-("
                                                  + DateUtils.BASIC_ISO_DATE_TIME_REGEX + ")-*");
        for (File file : archivedFiles) {
            Matcher matcher = fileDatePattern.matcher(file.getName());
            if (matcher.find()) {
                String dateString = matcher.group(1);
                LocalDateTime dateTime = LocalDateTime.parse(dateString, DateUtils.BASIC_ISO_DATE_TIME);
                if (dateTime.isBefore(LocalDateTime.now().minusMonths(monthsToKeep))) {
                    logger.info("Senate site archive file: '" + file + "' is more than '" + monthsToKeep + "' months old. Deleting.");
                    file.delete();
                }
            }
        }
    }

    private File getArchiveBillDir(SpotCheckRefType refType) throws IOException {
        return FileIOUtils.safeGetFolder(environment.getArchiveDir(), FsSenateSiteDao.SENSITE_DUMP_DIRNAME + "/" + refType.getRefName());
    }
}
