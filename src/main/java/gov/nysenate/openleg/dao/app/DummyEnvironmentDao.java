package gov.nysenate.openleg.dao.app;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.util.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class DummyEnvironmentDao implements EnvironmentDao
{
    private static final Logger logger = LoggerFactory.getLogger(DummyEnvironmentDao.class);

    @Value("${env.directory}") private String envDirectory;

    public DummyEnvironmentDao() {}

    @Override
    public Environment getActiveEnvironment() {
        logger.debug("Called get active environment : " + envDirectory);
        return new Environment(Application.getConfig(), "env", "master");
    }

    @Override
    public Map<String, Environment> getEnvironmentMap() {
        return null;
    }

    @Override
    public void addEnvironment(Environment env) {

    }

    @Override
    public void deleteEnvironment(Environment env) {

    }
}
