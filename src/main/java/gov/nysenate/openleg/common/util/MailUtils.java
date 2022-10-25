package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.config.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.mail.*;
import java.util.List;
import java.util.Properties;


/**
 * Contains methods that can be used to interact with mail servers
 */

@Service
public class MailUtils {

    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);

    private final String storeProtocol, smtpUser, smtpPass;
    private final Properties mailProperties;
    private final Environment environment;
    private Store store;
    private Folder sourceFolder, archiveFolder, partialFolder;

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
        this.mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);

        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.pass", smtpPass);
        mailProperties.put("mail.smtp.starttls.enable", stlsEnable);
        mailProperties.put("mail.smtp.ssl.enable", sslEnable);
        mailProperties.put("mail.smtp.ssl.protocols", sslProtocol);
        mailProperties.put("mail.smtp.user", smtpUser);

        if (!environment.getEmailFromAddress().isBlank())
            mailProperties.put("mail.smtp.from", environment.getEmailFromAddress());

        mailProperties.put("mail.debug", debug);
        mailProperties.put("mail.store.protocol", storeProtocol);
        mailProperties.put("mail.imaps.ssl.protocols", imapsSSLProtocol);
        mailProperties.put("mail.smtp.connectiontimeout", connTimeout);
        mailProperties.put("mail.smtp.timeout", smtpTimeout);
        mailProperties.put("mail.smtp.writetimeout", writeTimeout);

        this.environment = environment;
    }

    /**
     * Connects to the email store and folders when necessary, otherwise, it will reuse the existing connection.
     */
    public void createCheckMailConnection() throws MessagingException {
        if (this.store != null && this.store.isConnected()) {
            // Current store is still valid, we can continue to use it.
            return;
        }
        // Connection to the store has been lost, re-establish it.
        try {
            store = getStore(environment.getEmailHost(), environment.getEmailUser(), environment.getEmailPass());
            this.sourceFolder = navigateToFolder(environment.getEmailReceivingFolder(), store);
            this.archiveFolder = navigateToFolder(environment.getEmailProcessedFolder(), store);
            this.partialFolder = navigateToFolder(environment.getEmailPartialDaybreakFolder(), store);
            if (sourceFolder != null)
                sourceFolder.open(Folder.READ_WRITE);
        } catch (MessagingException ex) {
            destroy();
            if (environment.isCheckmailEnabled()) {
                logger.info("Unable to connect to email account: " + environment.getEmailHost(), ex);
            }
        }
    }

    @PreDestroy
    private void destroy() {
        try {
            if (store != null)
                store.close();
        } catch (MessagingException ignored) {}
    }

    public Message[] getIncomingMessages() throws MessagingException {
        try {
            return sourceFolder == null || Thread.currentThread().isInterrupted() ?
                    new Message[0] : sourceFolder.getMessages();
        } catch (MessagingException ex) {
            // The connection was closed by the mail server. Disconnect so we know to reconnect on next use.
            destroy();
            throw ex;
        }
    }

    /**
     * Moves messages from the source folder to the partial or archive folder,
     * then deletes the emails from the source folder.
     */
    public void moveMessages(List<Message> messages, boolean toArchive) throws MessagingException {
        if (sourceFolder == null || Thread.currentThread().isInterrupted())
            return;
        try {
            sourceFolder.copyMessages(messages.toArray(new Message[0]), toArchive ? archiveFolder : partialFolder);
            for (Message message : messages)
                message.setFlag(Flags.Flag.DELETED, true);
            sourceFolder.expunge();
        } catch (MessagingException ex) {
            // The connection was closed by the mail server. Disconnect so we know to reconnect on next use.
            destroy();
            throw ex;
        }
    }

    /**
     * Gets an authenticated smtp mail session
     *
     * @return Session
     */
    public Session getSmtpSession() {
        var auth = new Authenticator() {
            private final PasswordAuthentication pa = new PasswordAuthentication(smtpUser, smtpPass);

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        };
        return Session.getInstance(mailProperties, auth);
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
        // A connection shouldn't be attempted if the program is being shutdown.
        if (Thread.currentThread().isInterrupted())
            return null;
        Store store = Session.getInstance(mailProperties).getStore(storeProtocol);
        try {
            store.connect(host, user, password);
        } catch (MessagingException ex) {
            store.close();
            throw ex;
        }
        return store;
    }

    /**
     * Navigates through the given mail store to get the folder specified by the given path
     *
     * @param path  The path to navigate to
     * @param store The mail store to navigate through
     * @return Folder - the resulting folder
     * @throws MessagingException If the folder cannot be found
     */
    private static Folder navigateToFolder(String path, Store store) throws MessagingException {
        if (store == null)
            return null;
        String[] splitPath = path.split("/");
        Folder folder = store.getFolder(splitPath[0]);
        for (int i = 1; i < splitPath.length; i++)
            folder = folder.getFolder(splitPath[i]);
        return folder;
    }
}
