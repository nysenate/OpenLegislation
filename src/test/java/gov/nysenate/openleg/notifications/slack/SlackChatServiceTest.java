package gov.nysenate.openleg.notifications.slack;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.notifications.slack.model.SlackAddress;
import gov.nysenate.openleg.notifications.slack.model.SlackAttachment;
import gov.nysenate.openleg.notifications.slack.model.SlackField;
import gov.nysenate.openleg.notifications.slack.model.SlackMessage;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Category(SillyTest.class)
public class SlackChatServiceTest extends BaseTests {
    private static final String testMessageText = "This is a test message from ";
    // Template for printing line info.  Inputs are class name, method name, and line number
    private static final String lineInfoTemplate = "%s#%s:%d";
    private static final List<String> slackers =
            List.of("slacker_A", "slacker_B", "", "   ");

    @Autowired
    private SlackChatService slackChatService;

    private static final List<SlackAddress> addresses =
            ImmutableList.<SlackAddress>builder()
//                    .addAll(getAddresses("openleg-notices", slackers))
                    .add(new SlackAddress("not-a-channel", "not_a_slacker"))
//                    .add(new SlackAddress("openleg-notices", "yet_another_slacker"))
//                    .add(new SlackAddress("openleg"))
//                    .add(new SlackAddress("dev", "hacky_slacker"))
//                    .add(new SlackAddress(SlackAddress.DEFAULT_CHANNEL_ID, "hash_slinging_slacker"))
                    .build();

    private static final SlackAttachment stockAttachment = new SlackAttachment()
            .setColor("#0099FF")
            .setFallback("XD")
            .setTitle("Hello!")
            .setTitleLink("google.com")
            .setPretext("this is a pretext")
            .addFields(new SlackField("field title", "field value"))
            .addFields(new SlackField("field title 2", "field value 2"));

    private static final SlackMessage stockTestMessage = new SlackMessage()
            .setText("stock test message from " +
                    SlackChatServiceTest.class.getCanonicalName())
            .setChannel("openleg-notices")
            .setMentions(slackers)
            .setUsername("openleg-test-bot")
            .setIcon(":robot_face:")
//            .addAttachments(stockAttachment)
//            .addAttachments(new SlackAttachment(stockAttachment)
//                    .setTitle("Hello Again!"))
            ;


    /* --- Tests --- */

    /**
     * Should post a message to the default channel configured 
     * within the slack webhook integration
     */
    @Test
    public void sendMessageTest() {
        slackChatService.sendMessage(testMessageText + getStackInfo());
    }

    /**
     * Should post a message to the default channel configured 
     * within the slack webhook integration
     * Will mention users in 'slackers' list except for the blank strings
     */
    @Test
    public void sendMentionMessageTest() {
        slackChatService.sendMessage(testMessageText + getStackInfo(), slackers);
    }

    /**
     * Should post a message with all the attributes of {@link #stockTestMessage}
     */
    @Test
    public void sendCustomMessageTest() {
        SlackMessage message = new SlackMessage(stockTestMessage);
        message.setText(testMessageText + getStackInfo());
        slackChatService.sendMessage(message);
    }

    /**
     * Should post {@link #stockTestMessage} to all channels and addresses in {@link #addresses}
     */
    @Test
    public void sendRoutedMessageTest() {
        SlackMessage message = new SlackMessage(stockTestMessage);
        message.setText(testMessageText + getStackInfo());
        slackChatService.sendMessage(message, addresses);
    }

    /* --- Internal Methods --- */

    private static List<SlackAddress> getAddresses(String channel, List<String> slackers) {
        return slackers.stream()
                .map(slacker -> new SlackAddress(channel, slacker))
                .toList();
    }

    private static String getStackInfo() {
        StackTraceElement frame = Thread.currentThread().getStackTrace()[1];
        return String.format(lineInfoTemplate,
                frame.getClassName(), frame.getMethodName(), frame.getLineNumber());
    }
}

