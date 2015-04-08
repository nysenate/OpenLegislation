package gov.nysenate.openleg.util;

import gov.nysenate.openleg.config.Environment;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.util.Properties;

/**
 * Contains methods that can be used to interact with mail servers
 */

@Service
public class MailUtils {

    @Autowired private Environment environment;

    @Value("${mail.smtp.host}") private String host;
    @Value("${mail.smtp.port}") private String port;
    @Value("${mail.smtp.auth:false}") private boolean auth;
    @Value("${mail.smtp.user:}") private String smtpUser;
    @Value("${mail.smtp.password:}") private String smtpPass;
    @Value("${mail.debug:false}") private boolean debug;
    @Value("${mail.smtp.starttls.enable:}") private boolean stlsEnable;

    @Value("${mail.store.protocol:imaps}") private String storeProtocol;
    @Value("${mail.imaps.ssl.protocols:SSLv3}") private String imapsSSLProtocol;

    private Properties mailProperties;

    @PostConstruct
    public void init() {
        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);

        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", stlsEnable);

        mailProperties.put("mail.smtp.user", smtpUser);
        mailProperties.put("mail.smtp.pass", smtpPass);

        mailProperties.put("mail.debug", debug);

        mailProperties.put("mail.store.protocol", storeProtocol);
        mailProperties.put("mail.imaps.ssl.protocols", imapsSSLProtocol);
    }

    /**
     * Gets an authenticated smtp mail session
     * @return Session
     */
    public Session getSmtpSession() {
        return Session.getInstance(mailProperties, getSmtpAuthenticator());
    }

    /**
     * Gets a mail session for use with imaps
     * @return
     */
    public Session getImapsSession() {
        return Session.getInstance(mailProperties);
    }

    public Properties getMailProperties() {
        return mailProperties;
    }

    /**
     * Establishes a connection to a mail server and returns the resulting connection object
     * The store must be closed on its own.
     *
     * @param host the mail host
     * @param user the username of the mail account
     * @param password the password for the username
     * @return Store
     * @throws MessagingException if a connection cannot be established
     */
    public Store getStore(String host, String user, String password)
            throws MessagingException {
        Store store = getImapsSession().getStore(storeProtocol);
        try {
            store.connect(host, user, password);
        }
        catch (MessagingException ex) {
            store.close();
            throw ex;
        }
        return store;
    }

    public Store getCheckMailStore() throws MessagingException {
        return getStore(environment.getEmailHost(), environment.getEmailUser(), environment.getEmailPass());
    }

    /**
     * Navigates through the given mail store to get the folder specified by the given path
     *
     * @param path The path to navigate to
     * @param store The mail store to navigate through
     * @return Folder - the resulting folder
     * @throws MessagingException If the folder cannot be found
     */
    public Folder navigateToFolder(String path, Store store) throws MessagingException {
        String[] splitPath = path.split("/");
        Folder folder = store.getFolder(splitPath[0]);
        for(int i=1; i<splitPath.length; i++) {
            folder = folder.getFolder(splitPath[i]);
        }
        return folder;
    }

    /** --- Internal Methods --- */

    /**
     * Generates an authenticator from the smtp properties
     */
    private Authenticator getSmtpAuthenticator() {
        return new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(smtpUser, smtpPass);
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
    }

}
