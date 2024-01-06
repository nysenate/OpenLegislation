package gov.nysenate.openleg;

import gov.nysenate.openleg.config.ConsoleApplicationConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, ConsoleApplicationConfig.class})
@ActiveProfiles("test")
@Transactional
public abstract class BaseTests {}
