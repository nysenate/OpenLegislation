package gov.nysenate.openleg.model.notification;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class NotificationTypeTest {

    /**
     * Ensures that parent-child relationships are all 2 way.
     */
    @Test
    public void parentChildTest() {
        for (NotificationType type : NotificationType.values()) {
            if (type.getParent() == null) {
                assertEquals("Only the ALL type can have a null parent", NotificationType.ALL, type);
            } else {
                assertTrue("A notification type's parent should have it registered as a child",
                        type.getParent().getChildren().contains(type));
            }
            for (NotificationType child : type.getChildren()) {
                assertTrue("All children of a notification type should have the type registered as their parent.",
                        child.getParent() == type);
            }
        }
    }

    /**
     * Ensures there are no cycles.
     *
     * Relies on 2 way parent-child relationships tested in {@link #parentChildTest()}
     */
    @Test
    public void noCyclesTest() {
        for (NotificationType type : NotificationType.values()) {
            EnumSet<NotificationType> visited = EnumSet.noneOf(NotificationType.class);
            for (NotificationType t = type; t != null; t = t.getParent()) {
                assertFalse("Nodes cannot reoccur in path from node to root", visited.contains(t));
                visited.add(t);
            }
        }
    }
}