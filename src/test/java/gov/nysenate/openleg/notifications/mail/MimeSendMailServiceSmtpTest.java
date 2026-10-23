package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.notifications.mail.SendResult.Outcome;
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
 * since whether a failure is unconfirmed depends on how far the transport got.
 */
@Category(UnitTest.class)
public class MimeSendMailServiceSmtpTest {

    private enum Script { ACCEPT, STALL_AT_RCPT, REFUSE_RECIPIENT, CLOSE_AFTER_DATA, REFUSE_SENDER }

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
    public void connectionClosedAfterTheWholeMessageIsUnconfirmed() throws Exception {
        SendResult result = send(Script.CLOSE_AFTER_DATA);

        assertEquals(Outcome.UNCONFIRMED, result.outcome());
        assertEquals("[EOF]", result.cause().getMessage().strip());
    }

    @Test(timeout = 10_000)
    public void refusedSenderIsAServerFailure() throws Exception {
        SendResult result = send(Script.REFUSE_SENDER);

        assertEquals(Outcome.SERVER_FAILED, result.outcome());
        assertEquals("530 5.7.0 Authentication required", result.cause().getMessage().strip());
    }

    private SendResult send(Script script) throws Exception {
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
        MailUtils mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(Session.getInstance(properties));
        var sender = new MimeSendMailService(mailUtils, mock(OpenLegEnvironment.class));
        MimeMessage message = sender.createMessage();
        message.setFrom(new InternetAddress("openleg@example.org"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("r@example.com"));
        message.setSubject("Notice");
        message.setText("Body");
        return sender.trySend(message);
    }

    private void converse(Socket socket, Script script) {
        try (socket;
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
             var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII)) {
            reply(out, "220 test");
            String line;
            while ((line = in.readLine()) != null) {
                String command = line.toUpperCase();
                if (command.startsWith("EHLO") || command.startsWith("HELO")) {
                    reply(out, "250 test");
                } else if (command.startsWith("MAIL")) {
                    reply(out, script == Script.REFUSE_SENDER ? "530 5.7.0 Authentication required" : "250 OK");
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
