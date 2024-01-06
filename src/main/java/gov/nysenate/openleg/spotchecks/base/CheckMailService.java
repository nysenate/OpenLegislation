package gov.nysenate.openleg.spotchecks.base;

@FunctionalInterface
public interface CheckMailService {
    /**
     * Checks a mail server for specific emails, saving them to the filesystem if found.
     * @return the number of emails saved
     */
    int checkMail();
}
