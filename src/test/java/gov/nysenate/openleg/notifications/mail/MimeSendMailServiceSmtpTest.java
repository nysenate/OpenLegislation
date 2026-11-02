package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.SendResult.Outcome;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sends through the real Angus SMTP transport to a scripted server on the loopback interface,
 * since each outcome depends on the exception the transport throws at that point of the conversation.
 */
@Category(UnitTest.class)
public class MimeSendMailServiceSmtpTest {

    private enum Script {
        ACCEPT, STALL_AT_RCPT, REFUSE_RECIPIENT, CLOSE_AFTER_DATA, REFUSE_SENDER, REJECT_LOGIN,
        ONE_MESSAGE_PER_CONNECTION
    }

    private ServerSocket server;
    private final List<String> received = new CopyOnWriteArrayList<>();

    @After
    public void tearDown() throws IOException {
        if (server != null) {
            server.close();
        }
    }

    @Test(timeout = 10_000)
    public void acceptedMessageArrivesWhole() throws Exception {
        SendResult result = send(Script.ACCEPT);

        assertEquals(SendResult.SENT, result);
        assertTrue(received.toString(), received.contains("Subject: Notice"));
        assertTrue(received.toString(), received.contains("Body"));
    }

    @Test(timeout = 10_000)
    public void explicitMessageIdIsKept() throws Exception {
        MimeSendMailService sender = sender(Session.getInstance(serve(Script.ACCEPT)));
        MimeMessage message = message(sender);
        message.setHeader("Message-ID", "<notice-1@example.org>");

        assertEquals(SendResult.SENT, sender.trySend(message));

        assertTrue(received.toString(), received.contains("Message-ID: <notice-1@example.org>"));
    }

    @Test(timeout = 10_000)
    public void eachMessageHasAConnectionOfItsOwn() throws Exception {
        MimeSendMailService sender = sender(Session.getInstance(serve(Script.ONE_MESSAGE_PER_CONNECTION)));

        // The server refuses a second message on the same connection.
        assertEquals(SendResult.SENT, send(sender));
        assertEquals(SendResult.SENT, send(sender));
    }

    @Test(timeout = 10_000)
    public void timeoutBeforeTheMessageIsSentIsAServerFailure() throws Exception {
        SendResult result = send(Script.STALL_AT_RCPT);

        assertEquals(Outcome.SERVER_FAILED, result.outcome());
        assertEquals("Exception reading response", result.cause().getMessage());
    }

    @Test(timeout = 10_000)
    public void refusedRecipientIsRefused() throws Exception {
        SendResult result = send(Script.REFUSE_RECIPIENT);

        assertEquals(Outcome.REFUSED, result.outcome());
        assertEquals("Invalid Addresses", result.cause().getMessage());
    }

    @Test(timeout = 10_000)
    public void connectionClosedAfterTheWholeMessageIsAServerFailure() throws Exception {
        SendResult result = send(Script.CLOSE_AFTER_DATA);

        assertEquals(Outcome.SERVER_FAILED, result.outcome());
        assertEquals("[EOF]", result.cause().getMessage().strip());
    }

    @Test(timeout = 10_000)
    public void refusedSenderIsAServerFailure() throws Exception {
        SendResult result = send(Script.REFUSE_SENDER);

        assertEquals(Outcome.SERVER_FAILED, result.outcome());
        assertEquals("530 5.7.0 Authentication required", result.cause().getMessage().strip());
    }

    @Test(timeout = 10_000)
    public void rejectedLoginIsLoginRejected() throws Exception {
        Properties properties = serve(Script.REJECT_LOGIN);
        properties.put("mail.smtp.auth", "true");
        var session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("openleg", "wrong");
            }
        });

        SendResult result = send(sender(session));

        assertEquals(Outcome.LOGIN_REJECTED, result.outcome());
        assertEquals("535 5.7.8 Authentication credentials invalid", result.cause().getMessage().strip());
    }

    @Test(timeout = 10_000)
    public void unreachableServerIsNotConnected() throws Exception {
        int port;
        try (var socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            port = socket.getLocalPort();
        }
        var properties = new Properties();
        properties.put("mail.smtp.host", "127.0.0.1");
        properties.put("mail.smtp.port", String.valueOf(port));
        // Bounded even if another process takes the freed port and never answers.
        properties.put("mail.smtp.connectiontimeout", "1000");
        properties.put("mail.smtp.timeout", "1000");

        SendResult result = send(sender(Session.getInstance(properties)));

        assertEquals(Outcome.NOT_CONNECTED, result.outcome());
        String reason = result.cause().getMessage();
        assertTrue(reason, reason.startsWith("Couldn't connect to host, port: 127.0.0.1, " + port));
    }

    private SendResult send(Script script) throws Exception {
        return send(sender(Session.getInstance(serve(script))));
    }

    /** Starts a server that follows the script, and returns the settings to reach it. */
    private Properties serve(Script script) throws IOException {
        server = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        Thread acceptor = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = server.accept();
                    new Thread(() -> converse(socket, script)).start();
                }
            } catch (IOException closed) {
                // The test is over.
            }
        });
        acceptor.setDaemon(true);
        acceptor.start();

        var properties = new Properties();
        properties.put("mail.smtp.host", "127.0.0.1");
        properties.put("mail.smtp.port", String.valueOf(server.getLocalPort()));
        properties.put("mail.smtp.connectiontimeout", "1000");
        properties.put("mail.smtp.timeout", "1000");
        return properties;
    }

    private static MimeSendMailService sender(Session session) {
        MailUtils mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        return new MimeSendMailService(mailUtils, mock(OpenLegEnvironment.class));
    }

    private static SendResult send(MimeSendMailService sender) throws Exception {
        return sender.trySend(message(sender));
    }

    private static MimeMessage message(MimeSendMailService sender) throws Exception {
        MimeMessage message = sender.createMessage();
        message.setFrom(new InternetAddress("openleg@example.org"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("r@example.com"));
        message.setSubject("Notice");
        message.setText("Body");
        return message;
    }

    private void converse(Socket socket, Script script) {
        try (socket;
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
             var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII)) {
            reply(out, "220 test");
            int messages = 0;
            String line;
            while ((line = in.readLine()) != null) {
                String command = line.toUpperCase();
                if (command.startsWith("EHLO") && script == Script.REJECT_LOGIN) {
                    reply(out, "250-test");
                    reply(out, "250 AUTH PLAIN");
                } else if (command.startsWith("EHLO") || command.startsWith("HELO")) {
                    reply(out, "250 test");
                } else if (command.startsWith("AUTH")) {
                    reply(out, "535 5.7.8 Authentication credentials invalid");
                } else if (command.startsWith("MAIL")) {
                    if (script == Script.REFUSE_SENDER) {
                        reply(out, "530 5.7.0 Authentication required");
                    } else if (script == Script.ONE_MESSAGE_PER_CONNECTION && messages > 0) {
                        reply(out, "421 4.7.0 Too many messages in this connection");
                    } else {
                        reply(out, "250 OK");
                    }
                } else if (command.startsWith("RCPT")) {
                    if (script == Script.STALL_AT_RCPT) {
                        Thread.sleep(3000);
                        return;
                    }
                    reply(out, script == Script.REFUSE_RECIPIENT ? "550 5.1.1 No such user" : "250 OK");
                } else if (command.startsWith("DATA")) {
                    reply(out, "354 Go ahead");
                    while (!(line = in.readLine()).equals(".")) {
                        received.add(line);
                    }
                    if (script == Script.CLOSE_AFTER_DATA) {
                        return;
                    }
                    messages++;
                    reply(out, "250 Queued");
                } else if (command.startsWith("QUIT")) {
                    reply(out, "221 Bye");
                    return;
                } else {
                    reply(out, "250 OK");
                }
            }
        } catch (IOException | InterruptedException ignored) {
            // The client closed the connection.
        }
    }

    private static void reply(PrintWriter out, String line) {
        out.print(line + "\r\n");
        out.flush();
    }
}
