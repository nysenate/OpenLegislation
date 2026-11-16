package gov.nysenate.openleg.config.annotation;

/**
 * JUnit category for silly tests, i.e. maybe non-reproducible and not really intended to pass.
 * Use it as {@code @Category(SillyTest.class)}; surefire and failsafe exclude such classes.
 */
public interface SillyTest {}
