package gov.nysenate.openleg.common.util;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public class SimpleTimer {
    private LocalTime timer;
    private String message = "";

    public SimpleTimer() {
        System.err.println("Warning! This class should only be used to help with testing.");
    }

    public void startTimer(String message) {
        this.message = message;
        timer = LocalTime.now();
    }

    public void stopTimer() {
        System.out.print(message + ": ");
        if (timer == null) {
            System.err.println("Error! Timer was not started.");
        }
        System.out.println(timer.until(LocalTime.now(), ChronoUnit.SECONDS));
        timer = null;
        message = "";
    }
}
