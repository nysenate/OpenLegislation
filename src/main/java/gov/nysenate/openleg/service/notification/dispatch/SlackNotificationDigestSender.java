package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.slack.SlackAttachment;
import gov.nysenate.openleg.service.slack.SlackChatService;
import gov.nysenate.openleg.service.slack.SlackField;
import gov.nysenate.openleg.service.slack.SlackMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@Service
public class SlackNotificationDigestSender extends BaseSlackNotificationSender implements NotificationDigestSender {

    private static final ImmutableSet<NotificationTarget> targets = ImmutableSet.of(NotificationTarget.SLACK);

    private static final String digestColor = "#0099FF";
    private static final String digestIcon = ":piggy:";

    @Override
    public Set<NotificationTarget> getTargets() {
        return targets;
    }

    @Override
    public void sendDigest(NotificationDigest digest) {
        String digestText = NotificationDigestFormatter.getDigestText(digest, this::getDisplayUrl);
        SlackMessage message = new SlackMessage()
                .addAttachments(new SlackAttachment()
                        .setTitle(NotificationDigestFormatter.getSummary(digest))
                        .setTitleLink(getDigestUrl(digest))
                        .setText(digestText)
                        .setFallback(truncateDigest(digest, digestText))
                        .setColor(digestColor)
                        .setFields(getFields(digest)))
                .setText("")
                .setIcon(digestIcon);
        slackChatService.sendMessage(message, Collections.singleton(digest.getAddress()));
    }

    /** --- Internal Methods --- */

    private ArrayList<SlackField> getFields(NotificationDigest digest) {
        return Lists.newArrayList(
                new SlackField("Type", digest.getType().toString()),
                new SlackField("From", digest.getStartDateTime().toString()),
                new SlackField("To", digest.getEndDateTime().toString()));
    }
}
