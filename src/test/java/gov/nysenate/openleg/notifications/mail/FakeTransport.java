package gov.nysenate.openleg.notifications.mail;

import jakarta.mail.Address;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Provider;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.URLName;
import jakarta.mail.internet.InternetAddress;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stands in for the SMTP transport of a session, recording delivered recipients and rejecting
 * or disconnecting as scripted, with the exceptions Angus throws for each case. The script is shared,
 * so tests using it must not run in parallel.
 */
public class FakeTransport extends Transport {
    /** Addresses whose messages are rejected at RCPT TO, as a server refusing the recipient would. */
    public static final Set<String> rejected = new HashSet<>();
    /** Addresses whose messages are transmitted but never acknowledged, as when the reply times out. */
    public static final Set<String> unacknowledged = new HashSet<>();
    /** Addresses whose messages are transmitted, after which the server closes the connection without replying. */
    public static final Set<String> closedAfterData = new HashSet<>();
    /** Addresses whose messages fail with the given exception before any of the message is transmitted. */
    public static final Map<String, MessagingException> thrown = new HashMap<>();
    /** Recipients of every message accepted since the last reset. */
    public static final List<String> delivered = new ArrayList<>();
    /** Every message accepted since the last reset, as sent. */
    public static final List<Message> deliveredMessages = new ArrayList<>();
    /** The number of connections that succeed before every further one is refused. */
    public static int connectionsAllowed;
    /** The number of messages one connection accepts before refusing further ones while staying open. */
    public static int messagesPerConnection;
    /** Connection attempts, counted from 1, that fail as a reset handshake would. */
    public static final Set<Integer> failedConnectionAttempts = new HashSet<>();
    /** If set, the server refuses every sender at MAIL FROM, as one requiring a login would. */
    public static boolean refuseSender;
    public static boolean rejectCredentials;
    public static int connections;
    private static int connectionAttempts;

    public static void install(Session session) throws NoSuchProviderException {
        session.setProvider(new Provider(Provider.Type.TRANSPORT, "smtp", FakeTransport.class.getName(),
                "OpenLegislation tests", null));
    }

    public static void reset() {
        rejected.clear();
        unacknowledged.clear();
        closedAfterData.clear();
        thrown.clear();
        failedConnectionAttempts.clear();
        refuseSender = false;
        connectionAttempts = 0;
        delivered.clear();
        deliveredMessages.clear();
        connectionsAllowed = Integer.MAX_VALUE;
        messagesPerConnection = Integer.MAX_VALUE;
        rejectCredentials = false;
        connections = 0;
    }

    private int sentOnThisConnection;

    public FakeTransport(Session session, URLName urlName) {
        super(session, urlName);
    }

    @Override
    protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
        if (rejectCredentials) {
            throw new AuthenticationFailedException("535 Authentication failed");
        }
        connectionAttempts++;
        if (connections >= connectionsAllowed || failedConnectionAttempts.contains(connectionAttempts)) {
            throw new MessagingException("Couldn't connect to host",
                    new java.net.ConnectException("Connection refused"));
        }
        connections++;
        return true;
    }

    @Override
    public void sendMessage(Message message, Address[] addresses) throws MessagingException {
        String address = addresses[0].toString();
        if (sentOnThisConnection >= messagesPerConnection) {
            throw new SMTPSendFailedException("MAIL FROM:<openleg@example.org>", 421,
                    "421 4.7.0 Too many messages in this connection", null, null, null, null);
        }
        if (refuseSender) {
            throw new SMTPSendFailedException("MAIL FROM:<openleg@example.org>", 530,
                    "530 5.7.0 Authentication required", null, null, null, null);
        }
        if (rejected.contains(address)) {
            throw new SendFailedException("Invalid Addresses", new SMTPAddressFailedException(
                    (InternetAddress) addresses[0], "RCPT TO:<" + address + ">", 451,
                    "451 4.3.0 Mailbox temporarily unavailable"));
        }
        if (thrown.containsKey(address)) {
            throw thrown.get(address);
        }
        try {
            message.writeTo(OutputStream.nullOutputStream());
        } catch (IOException ex) {
            throw new MessagingException("IOException while sending message", ex);
        }
        delivered.add(address);
        deliveredMessages.add(message);
        sentOnThisConnection++;
        if (unacknowledged.contains(address)) {
            close();
            throw new MessagingException("Exception reading response", new SocketTimeoutException("Read timed out"));
        }
        if (closedAfterData.contains(address)) {
            close();
            throw new SMTPSendFailedException(".", -1, "[EOF]", null, null, null, null);
        }
    }
}
