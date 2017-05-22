package gov.nysenate.openleg.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Labels an integration test
 * Integration tests may test more than one module and depend on external data and services
 * (unlike a {@link UnitTest})
 */
@Retention(RetentionPolicy.SOURCE)
public @interface IntegrationTest {
}

