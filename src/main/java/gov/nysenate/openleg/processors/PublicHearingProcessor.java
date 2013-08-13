package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.PublicHearing;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class PublicHearingProcessor {
    private final Logger logger;

    private final int MINIMUM_SPEAKER_TUPLE_LENGTH = 3;

    private final String EMPTY_LINE = "^\\s*$";
    private final String BLANK_LINE = "^\\s*(\\d+)?\\s*$";
    private final String PAGE_HEAD = "^\\s*(\\d+)\\s*$";
    private final String FIRST_LINE = "^\\s*1\\s*.+$";
    private final String LAST_LINE = "^\\s*25.*$";
    private final String LINE_SEP = "^\\s*(\\d+)?\\s*\\-+\\s*$";

    PublicHearing publicHearing = new PublicHearing();

    boolean head = true;

    /*
     * head toggles
     */

    boolean committeeToggle = true;
    boolean titleToggle = true;
    boolean locationToggle = true;
    boolean timeToggle = true;

    /*
     * body toggles
     */

    boolean presidingToggle = false;
    boolean presentToggle = false;
    boolean senateToggle = false;
    boolean assemblyToggle = false;
    boolean speakersToggle = false;
    boolean bodyToggle = false;

    StringBuffer committee = new StringBuffer();
    StringBuffer title = new StringBuffer();
    StringBuffer location = new StringBuffer();
    StringBuffer time = new StringBuffer();

    StringBuffer presiding = new StringBuffer();
    StringBuffer senateMembers = new StringBuffer();
    StringBuffer assemblyMembers = new StringBuffer();
    StringBuffer speakers = new StringBuffer();
    StringBuffer currentSpeakerBuffer = new StringBuffer();
    StringBuffer body = new StringBuffer();

    Pattern speakerPattern = Pattern.compile("(.+)\\s++(\\d+)\\s+(\\d+)");
    Matcher matcher;

    public PublicHearingProcessor() {
        logger = Logger.getLogger(this.getClass());
    }

    public void process(File file, Storage storage) throws NumberFormatException, IOException {
        ArrayList<String> lineList = new ArrayList<String>();

        int pageNumber = -1;

        String prev = "";

        boolean readPage = false;

        for (String cur : FileUtils.readLines(file, "latin1")) {
            //beginning of page
            if(prev.matches(PAGE_HEAD) && cur.matches(FIRST_LINE)) {
                pageNumber = new Integer(prev.replaceAll(PAGE_HEAD, "$1"));

                lineList.add(cur);

                readPage = true;
            }
            //end of page
            else if(cur.matches(EMPTY_LINE) && prev.matches(LAST_LINE)) {
                parsePage(lineList, pageNumber);

                lineList.clear();

                readPage = false;
            }
            //on page
            else if(readPage) {
                lineList.add(cur);
            }

            prev = cur;
        }

        // TODO: Generate unique id's for public hearings
        String id = "";
        storage.set(publicHearing);
    }

    private String trimLine(String line) {
        return trimLine(line, " ");
    }

    private String trimLine(String line, String sep) {
        line = line.replaceAll("(?!^\\s*\\d++(\\w|\\:))^\\s*\\d+","").trim();

        if(line.matches(EMPTY_LINE))
            return "";
        return TextFormatter.append(line, sep);
    }

    private void parsePage(ArrayList<String> lineList, int pageNumber) {
        int count = 0;
        int spacing = getSpacing(lineList.get(0));

        for(String line:lineList) {
            if(head) {
                if(committeeToggle) {
                    if(line.matches(LINE_SEP)) {
                        committeeToggle = false;

                        String[] committees = committee.toString().split(" ON ");

                        for(int i = 1; i < committees.length; i++) {
                            publicHearing.committees.add(committees[i].split(" AND SENATE")[0].trim());
                        }

                        continue;
                    }
                    committee.append(trimLine(line));
                }
                else if(titleToggle) {
                    if(line.matches(LINE_SEP)) {
                        titleToggle = false;

                        publicHearing.title = title.toString().trim();

                        continue;
                    }

                    title.append(trimLine(line));
                }
                else if(locationToggle) {
                    location.append(trimLine(line, ", "));

                    if((++count) == 5) {
                        count = 0;

                        location = new StringBuffer(location.toString().replaceAll(", $", ""));

                        publicHearing.location = location.toString().trim();

                        locationToggle = false;
                    }
                }
                else if(timeToggle) {
                    time.append(trimLine(line));

                    if((++count) == 3) {
                        count = 0;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm aa");

                        try {
                            publicHearing.timeStamp = sdf.parse(
                                    time.toString().replaceAll("(\\w)\\.(\\w)\\.", "$1$2"));
                        } catch (ParseException e) {
                            logger.error(e);
                        }

                        timeToggle = false;
                    }
                }
                else {
                    head = false;
                }
            }
            else {
                /*
                 * this line appears at the end of the header before the body text
                 * and after the body text before the closing sentence
                 */
                if(line.matches("^.*?\\*\\s*(\\*\\s*)+.*?$")) {
                    if(bodyToggle) {
                        bodyToggle = false;
                    }
                    else {
                        resetBodyToggles();
                        bodyToggle = true;

                        return;
                    }
                }
                else {
                    if(doBodyToggle(line))
                        continue;

                    if(presidingToggle) {
                        if(line.matches(BLANK_LINE)) {
                            doPresiding();
                            presiding.setLength(0);
                        }
                        else {
                            presiding.append(trimLine(line, "|"));
                        }
                    }
                    else if(presentToggle) {
                        if(!line.matches(BLANK_LINE)) {
                            if(line.contains("Senator")) {
                                doPresent(line, publicHearing.presentSenators, "Senator ");
                            }
                            else if(line.contains("Assemblyman")) {
                                doPresent(line, publicHearing.presentAssemblyPersons, "Assemblyman ");
                            }
                        }
                    }
                    else if(senateToggle) {
                        if(!line.matches(BLANK_LINE)) {
                            doPresent(line, publicHearing.presentSenators, "Senator ");
                        }
                    }
                    else if(assemblyToggle) {
                        if(!line.matches(BLANK_LINE)) {
                            doPresent(line, publicHearing.presentAssemblyPersons, "Assemblyman ");
                        }
                    }
                    else if(speakersToggle) {
                        if(line.matches(BLANK_LINE)) {
                            doSpeaker();
                            currentSpeakerBuffer.setLength(0);
                        }
                        else {
                            int curSpacing = getSpacing(line);

                            if(curSpacing > spacing) {
                                currentSpeakerBuffer.setLength(currentSpeakerBuffer.length() - 1);
                                currentSpeakerBuffer.append(" ").append(trimLine(line,""));
                            }
                            else {
                                currentSpeakerBuffer.append(trimLine(line, "|"));
                            }
                        }
                    }
                    else if(bodyToggle) {
                        if(!line.matches(BLANK_LINE)) {
                            body.append(trimLine(line, ""))
                            .append("\n");
                        }

                    }
                }
            }
        }

        if(body != null && body.length() != 0) {
            publicHearing.addPage(pageNumber, body.toString());
            body.setLength(0);
        }
    }

    private int getSpacing(String line) {
        int count = 0;
        while(!Character.isLetter(line.charAt(count))) count++;
        return count;
    }

    private void doPresiding() {
        if(presiding.length() > 0) {
            String[] tuple = presiding.toString().replaceAll(", $", "").split("\\|");

            if(tuple.length  >= 2) {
                PublicHearing.Person person = new PublicHearing.Person(
                        tuple[0].trim().replace("Senator ", ""),
                        tuple[1].trim(),
                        null,
                        null);

                if(tuple.length >= 3) {
                    person.setCommittee(tuple[2].replaceAll("(?i)^.+?Standing Committee on ",""));
                }

                publicHearing.addPerson(person,
                        publicHearing.presidingSenators);
            }
        }
    }

    private void doPresent(String line, ArrayList<PublicHearing.Person> persons, String remove) {
        publicHearing.addPerson(
                new PublicHearing.Person(
                        trimLine(line,"").replace(remove,""),
                        null,
                        null,
                        null),
                        persons);
    }

    private void doSpeaker() {
        if(currentSpeakerBuffer.length() > 0) {
            Integer page = null;
            Integer questions = null;
            String organization = null;

            String[] tuple = currentSpeakerBuffer.toString().replaceAll("\\|$", "").split("\\|");

            int length = tuple.length;

            if(length < MINIMUM_SPEAKER_TUPLE_LENGTH || length % 2 == 0) {
                //TODO log issue
            }
            else {
                PublicHearing.Person person = new PublicHearing.Person();

                matcher = speakerPattern.matcher(tuple[0]);

                if(matcher.find()) {
                    tuple[0] = matcher.group(1);
                    page = new Integer(matcher.group(2));
                    questions = new Integer(matcher.group(3));
                }
                organization = tuple[tuple.length - 1];

                for(int i = 0; i < (tuple.length - 2); i++) {
                    person = new PublicHearing.Person(tuple[i].trim(), tuple[++i], null, organization);
                    person.setPage(page);
                    person.setQuestions(questions);
                    publicHearing.addPerson(person, publicHearing.speakers);
                }
            }
        }
    }

    private void resetBodyToggles() {
        presidingToggle = false;
        presentToggle = false;
        senateToggle = false;
        assemblyToggle = false;
        speakersToggle = false;
        bodyToggle = false;
    }

    private boolean doBodyToggle(String line) {
        if(line.matches("(?i)^.*?PRESIDING:.*?$")) {
            resetBodyToggles();
            presidingToggle = true;
        }
        else if(line.matches("(?i)^.*?PRESENT:.*?$")) {
            resetBodyToggles();
            presentToggle = true;
        }
        else if(line.matches("(?i)^.*?SENATE MEMBERS PRESENT:.*?$")) {
            resetBodyToggles();
            senateToggle = true;
        }
        else if(line.matches("(?i)^.*?ASSEMBLY MEMBERS PRESENT:.*?$")) {
            resetBodyToggles();
            assemblyToggle = true;
        }
        else if(line.matches("(?i)^.*?SPEAKERS(/LIST OF PARTICIPANTS|\\:).*?$")) {
            resetBodyToggles();
            speakersToggle = true;
        }
        else if(line.matches("(?i)^.*?PRESENT, Continued:.*?$")) {
            //continue reading
        }
        else if(line.matches("(?i)^.*?SPEAKERS, Continued:.*?$")) {
            //continue reading
        }
        else {
            return false;
        }
        return true;
    }
}
