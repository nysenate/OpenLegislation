package gov.nysenate.openleg.service.slack;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.slack.SlackAddress;
import gov.nysenate.openleg.model.slack.SlackAttachment;
import gov.nysenate.openleg.model.slack.SlackField;
import gov.nysenate.openleg.model.slack.SlackMessage;
import gov.nysenate.openleg.util.DebugUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Category(SillyTest.class)
public class SlackChatServiceTest extends BaseTests {

    @Autowired SlackChatService slackChatService;

    private static final String testMessageText = "This is a test message from ";

    private static final ImmutableList<String> slackers =
            ImmutableList.of("slacker_A", "slacker_B", "", "   ");

    private static final ImmutableList<SlackAddress> addresses =
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
        slackChatService.sendMessage(testMessageText + DebugUtils.getLineInfo());
    }


    /**
     * Should post a message to the default channel configured 
     * within the slack webhook integration
     * Will mention users in 'slackers' list except for the blank strings
     */
    @Test
    public void sendMentionMessageTest() {
        slackChatService.sendMessage(testMessageText + DebugUtils.getLineInfo(), slackers);
    }

    /**
     * Should post a message with all of the attributes of {@link #stockTestMessage}
     */
    @Test
    public void sendCustomMessageTest() {
        SlackMessage message = new SlackMessage(stockTestMessage);
        message.setText(testMessageText + DebugUtils.getLineInfo());
        slackChatService.sendMessage(message);
    }

    /**
     * Should post {@link #stockTestMessage} to all channels and addresses in {@link #addresses}
     */
    @Test
    public void sendRoutedMessageTest() {
        SlackMessage message = new SlackMessage(stockTestMessage);
        message.setText(testMessageText + DebugUtils.getLineInfo());
        slackChatService.sendMessage(message, addresses);
    }

    /* --- Internal Methods --- */

    private static List<SlackAddress> getAddresses(String channel, List<String> slackers) {
        return slackers.stream()
                .map(slacker -> new SlackAddress(channel, slacker))
                .collect(Collectors.toList());
    }
}

