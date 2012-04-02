package gov.nysenate.openleg.util;

import gov.nysenate.openleg.processors.TranscriptProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Williams
 *
 */
public class TranscriptFixer {

    NewTranscript t;

    public TranscriptFixer() {
        t = new NewTranscript();
    }

    public void doFix(File f) throws IOException {

        try {
            //TODO: This needs to finish porting at some point
            TranscriptProcessor parser = new TranscriptProcessor();
            //parser.parse(f);
        }
        catch (Exception e) {
            List<String> in;

            if((in = readContents(f)) != null) {

                List<String> ret = fix(in);

                BufferedWriter bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

                for(String s:ret) {
                    bw.write(s);
                    bw.newLine();
                }

                bw.close();
            }
        }
    }

    public List<String> fix(List<String> in) {

        //
        boolean errTog = true;

        boolean contents = false;

        String cur = null;

        List<String> ret = new ArrayList<String>();

        for(String s:in) {
            //if at the end of initial input (lines 1-25 containing date, location, senators, etc..)
            //have this so it doesn't try to reformat any of the recorded text
            if(s.contains("Candyco Transcription Service, Inc.")) {
                errTog = false;
            }

            //if reading through initial input
            if(errTog) {
                Pattern p = Pattern.compile("[\\d]{1,2}+");
                Matcher m = p.matcher(s);

                //if it can find a line number
                if(m.find()) {
                    cur = s.substring(m.start(), m.end());
                    if(cur.equals("1")) {
                        contents = true;
                    }
                    //at last line flip to false
                    if(cur.equals("25")) {
                        contents = false;
                    }
                }
            }

            //builds lines properly from information scraped from the og file
            if(contents || cur.equals("25")) {

                if(cur.equals("1")) {
                    s = "         1                 NEW YORK STATE SENATE";
                }
                else if(cur.equals("4")) {
                    s = "         4                          THE";
                }
                else if(cur.equals("5")) {
                    s = "         5                  STENOGRAPHIC RECORD";
                }
                else if(cur.equals("10")) {
                    s = "        10                   " + t.getLocation();
                }
                else if(cur.equals("11")) {
                    s = "        11                     " + t.getDate();
                }
                else if(cur.equals("12")) {
                    s = "        12                      " + t.getTime();
                }
                else if(cur.equals("15")) {
                    s = "        15                      " + t.getSession();
                }
                else if(cur.equals("18")) {
                    s = "        18       " + t.getSenators().get(0);
                }
                else if(cur.equals("19")) {
                    s = "        19       " + t.getSenators().get(1);
                }
                else {

                    if(cur.length() == 2) {
                        s = "        " + cur;
                    }
                    else {
                        s = "         " + cur;
                    }
                }

                //change value to prepare for any blank lines
                cur = "";
            }
            ret.add(s);
        }
        return ret;
    }

    /**
     * scrapes relevant information from file (date, time, location,etc...)
     * if it couldn't be processed due to formatting errors.
     *
     *
     * @param file the file to be read
     * @returns an arraylist of the lines in the file
     * @throws IOException for file read errors
     */
    public List<String> readContents(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        boolean toggle = true;

        boolean posSten = false;

        List<String> lines = new ArrayList<String>();

        String in = null;

        int count = 0;

        while((in = br.readLine()) != null) {
            if(in.contains("Candyco Transcription Service, Inc.")) {
                toggle = false;
            }
            //if in initial segment
            if(toggle) {

                //if the line containing stenographer information has been passed
                //the other information will necessarily come next
                if(posSten) {

                    //if line is not blank with only the line number
                    if(!in.matches("[\\s]*[\\d]{1,2}+[\\s]*")) {

                        Pattern p = Pattern.compile("[\\d]+");
                        Matcher m = p.matcher(in);

                        //get line number
                        if(m.find()) {
                            String str = in.substring(m.end());

                            str = str.trim();

                            //these come in the proper order, so we can assume one will come after the other
                            if(count == 0) {
                                t.setLocation(str);

                            }
                            else if(count == 1) {
                                t.setDate(str);
                            }
                            else if(count == 2) {
                                t.setTime(str);
                            }
                            else if(count == 3) {
                                t.setSession(str);
                            }
                            else {
                                t.addSenator(str);
                            }

                            count++;

                        }
                    }
                }

                if(in.contains("RECORD")) {
                    posSten = true;
                }
            }

            lines.add(in);

        }
        br.close();

        return lines;

    }
    //used to store information for when the file is recompiled
    public class NewTranscript {

        String location;

        String date;

        String time;

        String session;

        List<String> senators;

        public NewTranscript() {
            senators = new ArrayList<String>();
        }

        public String getLocation() {
            return location;
        }
        public String getDate() {
            return date;
        }
        public String getTime() {
            return time;
        }
        public String getSession() {
            return session;
        }
        public List<String> getSenators() {
            return senators;
        }


        public void setLocation(String location) {
            this.location = location;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public void setTime(String time) {
            this.time = time;
        }
        public void setSession(String session) {
            this.session = session;
        }
        public void setSenators(List<String> senators) {
            this.senators = senators;
        }

        public void addSenator(String s) {
            senators.add(s);
        }

        @Override
        public String toString() {
            return "NewTranscript [date=" + date + ", location=" + location
                    + ", senators=" + senators + ", session=" + session + ", time="
                    + time + "]";
        }
    }



}
