package gov.nysenate.openleg;

import gov.nysenate.openleg.util.Application;
import org.junit.BeforeClass;

public abstract class BaseTests
{
    @BeforeClass
    public static void bootstrap() {
        Application.bootstrap(Application.TEST_PROPERTY_FILENAME);
    }
}
