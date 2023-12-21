package gov.nysenate.openleg.config.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a test case as being silly, i.e. maybe non-reproducible and not really intended to pass.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface SillyTest {}
