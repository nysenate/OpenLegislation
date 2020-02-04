package gov.nysenate.openleg.processor;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.LegDataFragmentDao;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import gov.nysenate.openleg.processor.legdata.LegDataProcessor;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.data.CachedBillDataService;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Contains common methods used when testing {@link LegDataProcessor}s
 */
public abstract class BaseXmlProcessorTest extends BaseTests {

    @Autowired private CachedBillDataService billDataService;
    @Autowired private SourceFileRefDao sourceFileRefDao;
    @Autowired private LegDataFragmentDao legDataFragmentDao;
    @Autowired private Environment env;
    @Autowired private EventBus eventBus;
    @Autowired private List<LegDataProcessor> processors;

    private Map<LegDataFragmentType, LegDataProcessor> processorMap;

    private boolean originalIndexingSetting;
    private boolean originalScrapeQueueSetting;
    private boolean originalNotificationSetting;

    @PostConstruct
    public void init() {
        processorMap = Maps.uniqueIndex(processors, LegDataProcessor::getSupportedType);
    }

    /**
     * Store original indexing setting and disable indexing.
     * Elasticsearch does not support rollbacks,
     * so we need to prevent elasticsearch indexing when testing the processors.
     */
    @Before
    public void setUp() {
        originalIndexingSetting = env.isElasticIndexing();
        originalScrapeQueueSetting = env.isBillScrapeQueueEnabled();
        originalNotificationSetting = env.isNotificationsEnabled();
        env.setElasticIndexing(false);
        env.setBillScrapeQueueEnabled(false);
        env.setNotificationsEnabled(false);
    }

    /**
     * Restore original indexing setting.
     * Clear all caches.
     */
    @After
    public void cleanUp() {
        env.setElasticIndexing(originalIndexingSetting);
        env.setBillScrapeQueueEnabled(originalScrapeQueueSetting);
        env.setNotificationsEnabled(originalNotificationSetting);
        eventBus.post(new CacheEvictEvent(EnumSet.allOf(ContentCache.class)));
    }

    /**
     * Generates a dummy sobi fragment from an xml file
     *
     * @param xmlFilePath String - relative path to the xml file
     * @return {@link LegDataFragment}
     */
    protected LegDataFragment generateXmlSobiFragment(String xmlFilePath) {
        try {
            File file = FileIOUtils.getResourceFile(xmlFilePath);

            String contents = FileUtils.readFileToString(file, Charset.defaultCharset());

            XmlFile xmlFile = new XmlFile(file);

            LegDataFragmentType type = getFragmentType(contents);
            LegDataFragment legDataFragment = new LegDataFragment(xmlFile, type, contents, 0);

            sourceFileRefDao.updateSourceFile(xmlFile);
            legDataFragmentDao.updateLegDataFragment(legDataFragment);

            return legDataFragment;

        } catch (IOException | NullPointerException ex) {
            throw new IllegalArgumentException("Could not locate/read file " + xmlFilePath, ex);
        }
    }

    /**
     * Processes the given {@link LegDataFragment} using the appropriate {@link LegDataProcessor}
     *
     * @param fragment {@link LegDataFragment}
     */
    protected void processFragment(LegDataFragment fragment) {
        LegDataProcessor processor = processorMap.get(fragment.getType());
        processor.process(fragment);
        processor.postProcess();
        // Clear caches to ensure proper saves
        eventBus.post(new CacheEvictEvent(EnumSet.allOf(ContentCache.class)));
    }

    /**
     * Process the given xml file using this test's {@link LegDataProcessor}
     * This will perform all of the overhead steps to generate a {@link LegDataFragment} and process it
     *
     * @param xmlFilePath String - relative path to xml file
     */
    protected void processXmlFile(String xmlFilePath) {
        LegDataFragment legDataFragment = generateXmlSobiFragment(xmlFilePath);
        processFragment(legDataFragment);
    }

    /* --- Test helper methods --- */

    /**
     * Get a bill amendment from the db
     */
    protected BillAmendment getAmendment(BillId billId) throws BillNotFoundEx, BillAmendNotFoundEx {
        return getAmendment(billId, BillTextFormat.PLAIN);
    }

    /**
     * Get a bill amendment from the db
     */
    protected BillAmendment getAmendment(BillId billId, BillTextFormat htmlText) throws BillNotFoundEx, BillAmendNotFoundEx {
        Bill bill = billDataService.getBill(BaseBillId.of(billId), Collections.singleton(htmlText));
        return bill.getAmendment(billId.getVersion());
    }

    /**
     * Get a bill from the db.
     */
    protected Bill getBill(BillId billId) throws BillNotFoundEx {
        return billDataService.getBill(BaseBillId.of(billId));
    }

    /**
     * Does a BaseBillId exist in the database.
     */
    protected boolean doesBillExist(BillId billId) {
        try {
            getBill(BaseBillId.of(billId));
            return true;
        }
        catch (BillNotFoundEx ex) {
            return false;
        }
    }

    /* --- Internal Methods --- */

    private LegDataFragmentType getFragmentType(String text) {
        for (String line : text.split("\n")) {
            LegDataFragmentType type = LegDataFragmentType.matchFragmentType(line);
            if (type != null) {
                return type;
            }
        }
        throw new IllegalArgumentException("Could not identify fragment type of file");
    }
}
