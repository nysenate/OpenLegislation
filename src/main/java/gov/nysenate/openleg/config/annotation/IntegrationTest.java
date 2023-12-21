package gov.nysenate.openleg.config.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Labels an integration test
 * Integration tests may test more than one module (unlike a {@link UnitTest})
 */
@Retention(RetentionPolicy.SOURCE)
public @interface IntegrationTest {}
