package gov.nysenate.openleg.processor;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiFragmentDao;
import gov.nysenate.openleg.dao.sourcefiles.xml.XmlDao;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

/**
 * Contains common methods used when testing {@link SobiProcessor}s
 */
@Transactional
public abstract class BaseXmlProcessorTest extends BaseTests {

    @Autowired
    private SobiDao sobiDao;
    @Autowired
    private XmlDao xmlDao;
    @Autowired
    private SourceFileDao sourceFileDao;
    @Autowired
    private SobiFragmentDao sobiFragmentDao;

    /**
     * @return {@link SobiProcessor} the processor implementation associated with this test
     */
    abstract protected SobiProcessor getSobiProcessor();

    /**
     * Generates a dummy sobi fragment from an xml file
     *
     * @param xmlFilePath String - relative path to the xml file
     * @return {@link SobiFragment}
     */
    protected SobiFragment generateXmlSobiFragment(String xmlFilePath) {
        try {
            String absolutePath = getClass().getClassLoader().getResource(xmlFilePath).getFile();
            File file = new File(absolutePath);

            String contents = FileUtils.readFileToString(file);

            XmlFile xmlFile = new XmlFile(file);

            SobiFragmentType type = getSobiProcessor().getSupportedType();
            SobiFragment sobiFragment = new SobiFragment(xmlFile, type, contents, 0);

            sourceFileDao.updateSourceFile(xmlFile);
            sobiFragmentDao.updateSobiFragment(sobiFragment);

            return sobiFragment;

        } catch (IOException | NullPointerException ex) {
            throw new IllegalArgumentException("Could not locate/read file " + xmlFilePath, ex);
        }
    }

    /**
     * Processes the given {@link SobiFragment} using the test's {@link SobiProcessor}
     *
     * @param fragment {@link SobiFragment}
     */
    protected void processFragment(SobiFragment fragment) {
        SobiProcessor processor = getSobiProcessor();
        processor.process(fragment);
        processor.postProcess();
    }

    /**
     * Process the given xml file using this test's {@link SobiProcessor}
     * This will perform all of the overhead steps to generate a {@link SobiFragment} and process it
     *
     * @param xmlFilePath String - relative path to xml file
     */
    protected void processXmlFile(String xmlFilePath) {
        SobiFragment sobiFragment = generateXmlSobiFragment(xmlFilePath);
        processFragment(sobiFragment);
    }

}
