package gov.nysenate.openleg.service.spotcheck.base;

public interface CheckMailService {

    /**
     * Checks a mail server for specific emails, saving them to the filesystem if found
     * @return the number of emails saved
     */
    public int checkMail();
}
