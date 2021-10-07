package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.config.Environment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;

/**
 * Contains methods that can be used to interact with mail servers
 */

@Service
public class MailUtils {

    private final Environment environment;

    private final String storeProtocol;
    private final String smtpUser;
    private final String smtpPass;

    private final Properties mailProperties;

    public MailUtils(@Value("${mail.smtp.host}") String host,
                     @Value("${mail.smtp.port}") String port,
                     @Value("${mail.smtp.auth:false}") boolean auth,
                     @Value("${mail.smtp.user:}") String smtpUser,
                     @Value("${mail.smtp.password:}") String smtpPass,
                     @Value("${mail.debug:false}") boolean debug,
                     @Value("${mail.smtp.starttls.enable:false}") boolean stlsEnable,
                     @Value("${mail.smtp.ssl.enable:true}") boolean sslEnable,
                     @Value("${mail.smtp.ssl.protocols:TLSv1.2}") String sslProtocol,
                     @Value("${mail.store.protocol:imaps}") String storeProtocol,
                     @Value("${mail.imaps.ssl.protocols:SSLv3}") String imapsSSLProtocol,
                     @Value("${mail.smtp.connectiontimeout:5000}") String connTimeout,
                     @Value("${mail.smtp.timeout:5000}") String smtpTimeout,
                     @Value("${mail.smtp.writetimeout:5000}") String writeTimeout,
                     Environment environment) {
        this.storeProtocol = storeProtocol;
        this.smtpUser = smtpUser;
        this.smtpPass = smtpPass;
        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);

        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.pass", smtpPass);
        mailProperties.put("mail.smtp.starttls.enable", stlsEnable);
        mailProperties.put("mail.smtp.ssl.enable", sslEnable);
        mailProperties.put("mail.smtp.ssl.protocols", sslProtocol);

        mailProperties.put("mail.smtp.user", smtpUser);

        if (StringUtils.isNotBlank(environment.getEmailFromAddress())) {
            mailProperties.put("mail.smtp.from", environment.getEmailFromAddress());
        }

        mailProperties.put("mail.debug", debug);

        mailProperties.put("mail.store.protocol", storeProtocol);
        mailProperties.put("mail.imaps.ssl.protocols", imapsSSLProtocol);
        mailProperties.put("mail.smtp.connectiontimeout", connTimeout);
        mailProperties.put("mail.smtp.timeout", smtpTimeout);
        mailProperties.put("mail.smtp.writetimeout", writeTimeout);
        this.environment = environment;
    }


    /**
     * Gets an authenticated smtp mail session
     *
     * @return Session
     */
    public Session getSmtpSession() {
        return Session.getInstance(mailProperties, getSmtpAuthenticator());
    }

    /**
     * Gets a mail session for use with imaps
     *
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
     * @param host     the mail host
     * @param user     the username of the mail account
     * @param password the password for the username
     * @return Store
     * @throws MessagingException if a connection cannot be established
     */
    private Store getStore(String host, String user, String password)
            throws MessagingException {
        Store store = getImapsSession().getStore(storeProtocol);
        try {
            store.connect(host, user, password);
        } catch (MessagingException ex) {
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
     * @param path  The path to navigate to
     * @param store The mail store to navigate through
     * @return Folder - the resulting folder
     * @throws MessagingException If the folder cannot be found
     */
    public Folder navigateToFolder(String path, Store store) throws MessagingException {
        String[] splitPath = path.split("/");
        Folder folder = store.getFolder(splitPath[0]);
        for (int i = 1; i < splitPath.length; i++) {
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
