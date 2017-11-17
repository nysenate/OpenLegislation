package gov.nysenate.openleg.config.process;

/*
This class houses the processing configurations for a specific legislative year
Each year from 1995-now will have a configuration object (Even if they share a common configuration)
 */
public class ProcessYear {

    /**
     * Overarching Data Configurations
     * These configurations cover large groups of files
     * True = enabled | false = disabled
     */

    //Enable/Disable all xml processing
    private boolean allXml = true;

    //Enable/Disable all sobi processing
    private boolean allSobi = true;

    /**
     * Shared Data Configurations
     * These configurations are overarching, but shared between Xml and Sobi
     * True = enabled | false = disabled
     * For example, if a calendar in xml was sent through and allXml=true and sencal=true but allCalendar=false,
     * the calendar would not be processed
     */
    //Enable/Disable all calendar processing
    private boolean allCalendar = true;

    //Enable/Disable all active list processing
    private boolean allActiveLists = true;

    //Enable/Disable all agenda processing
    private boolean allAgendas = true;

    //Enable/Disable all agenda vote processing
    private boolean allAgendaVotes = true;

    //Enable/Disable all committee processing
    private boolean allCommittess = true;

    /**
     * Xml Specific Data Configurations
     * There is a configuration for each data type we receive in xml
     * The variables are named in accordance with the LBDC file extension on the files
     * True = enabled | false = disabled
     */

    private boolean anAct = true;

    private boolean apprMemo = true;

    private boolean billStat = true;

    private boolean billText = true;

    private boolean ldSumm = true;

    private boolean ldSpon = true;

    private boolean ldBlurb = true;

    private boolean sameas = true;

    private boolean senComm = true;

    private boolean senMemo = true;

    private boolean senFlVot = true;

    private boolean senCal = true;

    private boolean senCalal = true;

    private boolean senAen = true;

    private boolean senAgenV = true;

    private boolean vetoMsg = true;


    /**
     * Sobi Specific Data Configurations
     * There is a configuration for each case in the BillSobiProcessor and the other Sobi types in SobiFragmentType
     * They are named after the same cases as mentioned above
     * True = enabled | false = disabled
     */

    private boolean agenda = true;

    private boolean agendaVote = true;

    private boolean calendar = true;

    private boolean calendarActive = true;

    private boolean committee = true;

    private boolean annotation = true;

    //The var bill determines if the rest of the sobi data will be processed
    //For example if bill is false and the rest are true, no Sobi bill data will process
    //Enable/Disable all sobi bill processing
    private boolean bill = true;

    //The following are sub components of sobi bills
    private boolean billInfo = true;

    private boolean lawSection = true;

    private boolean title = true;

    private boolean billEvent = true;

    private boolean sameasSobi = true;

    private boolean sponsor = true;

    private boolean cosponsor = true;

    private boolean multisponsor = true;

    private boolean programInfo = true;

    private boolean actClause = true;

    private boolean law = true;

    private boolean summary = true;

    private boolean sponsorMemo = true;

    private boolean resolutionText = true;

    private boolean text = true;

    private boolean voteMemo = true;

    private boolean vetoApprMemo = true;

    //Default Constructor, By default everything is set true
    public ProcessYear() {}

    /*
    Overarching configuration methods
     */

    public void setOverarchingDataConfigs(boolean allXml, boolean allSobi) {
        this.allXml = allXml;
        this.allSobi = allSobi;
    }

    public void setOverarchingSharedConfigs(boolean allCalendar, boolean allActiveLists, boolean allCommittess,
                                            boolean allAgendas, boolean allAgendaVotes) {
        this.allCalendar = allCalendar;
        this.allActiveLists = allActiveLists;
        this.allCommittess = allCommittess;
        this.allAgendas = allAgendas;
        this.allAgendaVotes = allAgendaVotes;
    }

    /*
    Batch xml configuration methods
     */
    public void setAllXmlConfigsTrue() {
        this.anAct = true;
        this.apprMemo = true;
        this.billStat = true;
        this.billText = true;
        this.ldSumm = true;
        this.ldSpon = true;
        this. ldBlurb = true;
        this.sameas = true;
        this.senComm = true;
        this.senFlVot = true;
        this. senCal = true;
        this. senCalal = true;
        this.senAen = true;
        this. senAgenV = true;
        this.vetoMsg = true;
    }

    public void setAllXmlConfigsFalse() {
        this.anAct = false;
        this.apprMemo = false;
        this.billStat = false;
        this.billText = false;
        this.ldSumm = false;
        this.ldSpon = false;
        this. ldBlurb = false;
        this.sameas = false;
        this.senComm = false;
        this.senFlVot = false;
        this. senCal = false;
        this. senCalal = false;
        this.senAen = false;
        this. senAgenV = false;
        this.vetoMsg = false;
    }

    /*
    Batch sobi configuration methods
     */
    public void setGeneralSobiConfigsTrue() {
        this.agenda = true;
        this.agendaVote = true;
        this.calendar = true;
        this.calendarActive = true;
        this.committee = true;
        this.annotation = true;
        this.bill = true;
    }

    public void setGeneralSobiConfigsFalse() {
        this.agenda = false;
        this.agendaVote = false;
        this.calendar = false;
        this.calendarActive = false;
        this.committee = false;
        this.annotation = false;
        this.bill = false;
    }

    public void setSpecificSobiConfigsTrue() {
        this.billInfo = true;
        this.lawSection = true;
        this.title = true;
        this.billEvent = true;
        this.sameasSobi = true;
        this.sponsor = true;
        this.cosponsor = true;
        this.multisponsor = true;
        this.programInfo = true;
        this.actClause = true;
        this.law = true;
        this.summary = true;
        this.sponsorMemo = true;
        this.resolutionText = true;
        this.text = true;
        this.voteMemo = true;
        this.vetoApprMemo = true;
    }

    public void setSpecificSobiConfigsFalse() {
        this.billInfo = false;
        this.lawSection = false;
        this.title = false;
        this.billEvent = false;
        this.sameasSobi = false;
        this.sponsor = false;
        this.cosponsor = false;
        this.multisponsor = false;
        this.programInfo = false;
        this.actClause = false;
        this.law = false;
        this.summary = false;
        this.sponsorMemo = false;
        this.resolutionText = false;
        this.text = false;
        this.voteMemo = false;
        this.vetoApprMemo = false;
    }


    /*
    Individual Getter and Setter methods for all configuration variables
     */

    public boolean isAllXml() {
        return allXml;
    }

    public void setAllXml(boolean allXml) {
        this.allXml = allXml;
    }

    public boolean isAllSobi() {
        return allSobi;
    }

    public void setAllSobi(boolean allSobi) {
        this.allSobi = allSobi;
    }

    public boolean isAllCalendar() {
        return allCalendar;
    }

    public void setAllCalendar(boolean allCalendar) {
        this.allCalendar = allCalendar;
    }

    public boolean isAllActiveLists() {
        return allActiveLists;
    }

    public void setAllActiveLists(boolean allActiveLists) {
        this.allActiveLists = allActiveLists;
    }

    public boolean isAllAgendas() {
        return allAgendas;
    }

    public void setAllAgendas(boolean allAgendas) {
        this.allAgendas = allAgendas;
    }

    public boolean isAllAgendaVotes() {
        return allAgendaVotes;
    }

    public void setAllAgendaVotes(boolean allAgendaVotes) {
        this.allAgendaVotes = allAgendaVotes;
    }

    public boolean isAllCommittess() {
        return allCommittess;
    }

    public void setAllCommittess(boolean allCommittess) {
        this.allCommittess = allCommittess;
    }

    public boolean isAnAct() {
        return anAct;
    }

    public void setAnAct(boolean anAct) {
        this.anAct = anAct;
    }

    public boolean isApprMemo() {
        return apprMemo;
    }

    public void setApprMemo(boolean apprMemo) {
        this.apprMemo = apprMemo;
    }

    public boolean isBillStat() {
        return billStat;
    }

    public void setBillStat(boolean billStat) {
        this.billStat = billStat;
    }

    public boolean isBillText() {
        return billText;
    }

    public void setBillText(boolean billText) {
        this.billText = billText;
    }

    public boolean isLdSumm() {
        return ldSumm;
    }

    public void setLdSumm(boolean ldSumm) {
        this.ldSumm = ldSumm;
    }

    public boolean isLdSpon() {
        return ldSpon;
    }

    public void setLdSpon(boolean ldSpon) {
        this.ldSpon = ldSpon;
    }

    public boolean isLdBlurb() {
        return ldBlurb;
    }

    public void setLdBlurb(boolean ldBlurb) {
        this.ldBlurb = ldBlurb;
    }

    public boolean isSameas() {
        return sameas;
    }

    public void setSameas(boolean sameas) {
        this.sameas = sameas;
    }

    public boolean isSenComm() {
        return senComm;
    }

    public void setSenComm(boolean senComm) {
        this.senComm = senComm;
    }

    public boolean isSenMemo() {
        return senMemo;
    }

    public void setSenMemo(boolean senMemo) {
        this.senMemo = senMemo;
    }

    public boolean isSenFlVot() {
        return senFlVot;
    }

    public void setSenFlVot(boolean senFlVot) {
        this.senFlVot = senFlVot;
    }

    public boolean isSenCal() {
        return senCal;
    }

    public void setSenCal(boolean senCal) {
        this.senCal = senCal;
    }

    public boolean isSenCalal() {
        return senCalal;
    }

    public void setSenCalal(boolean senCalal) {
        this.senCalal = senCalal;
    }

    public boolean isSenAen() {
        return senAen;
    }

    public void setSenAen(boolean senAen) {
        this.senAen = senAen;
    }

    public boolean isSenAgenV() {
        return senAgenV;
    }

    public void setSenAgenV(boolean senAgenV) {
        this.senAgenV = senAgenV;
    }

    public boolean isVetoMsg() {
        return vetoMsg;
    }

    public void setVetoMsg(boolean vetoMsg) {
        this.vetoMsg = vetoMsg;
    }

    public boolean isAgenda() {
        return agenda;
    }

    public void setAgenda(boolean agenda) {
        this.agenda = agenda;
    }

    public boolean isAgendaVote() {
        return agendaVote;
    }

    public void setAgendaVote(boolean agendaVote) {
        this.agendaVote = agendaVote;
    }

    public boolean isCalendar() {
        return calendar;
    }

    public void setCalendar(boolean calendar) {
        this.calendar = calendar;
    }

    public boolean isCalendarActive() {
        return calendarActive;
    }

    public void setCalendarActive(boolean calendarActive) {
        this.calendarActive = calendarActive;
    }

    public boolean isCommittee() {
        return committee;
    }

    public void setCommittee(boolean committee) {
        this.committee = committee;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }

    public boolean isBill() {
        return bill;
    }

    public void setBill(boolean bill) {
        this.bill = bill;
    }

    public boolean isBillInfo() {
        return billInfo;
    }

    public void setBillInfo(boolean billInfo) {
        this.billInfo = billInfo;
    }

    public boolean isLawSection() {
        return lawSection;
    }

    public void setLawSection(boolean lawSection) {
        this.lawSection = lawSection;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isBillEvent() {
        return billEvent;
    }

    public void setBillEvent(boolean billEvent) {
        this.billEvent = billEvent;
    }

    public boolean isSameasSobi() {
        return sameasSobi;
    }

    public void setSameasSobi(boolean sameasSobi) {
        this.sameasSobi = sameasSobi;
    }

    public boolean isSponsor() {
        return sponsor;
    }

    public void setSponsor(boolean sponsor) {
        this.sponsor = sponsor;
    }

    public boolean isCosponsor() {
        return cosponsor;
    }

    public void setCosponsor(boolean cosponsor) {
        this.cosponsor = cosponsor;
    }

    public boolean isMultisponsor() {
        return multisponsor;
    }

    public void setMultisponsor(boolean multisponsor) {
        this.multisponsor = multisponsor;
    }

    public boolean isProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(boolean programInfo) {
        this.programInfo = programInfo;
    }

    public boolean isActClause() {
        return actClause;
    }

    public void setActClause(boolean actClause) {
        this.actClause = actClause;
    }

    public boolean isLaw() {
        return law;
    }

    public void setLaw(boolean law) {
        this.law = law;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public boolean isSponsorMemo() {
        return sponsorMemo;
    }

    public void setSponsorMemo(boolean sponsorMemo) {
        this.sponsorMemo = sponsorMemo;
    }

    public boolean isResolutionText() {
        return resolutionText;
    }

    public void setResolutionText(boolean resolutionText) {
        this.resolutionText = resolutionText;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public boolean isVoteMemo() {
        return voteMemo;
    }

    public void setVoteMemo(boolean voteMemo) {
        this.voteMemo = voteMemo;
    }

    public boolean isVetoApprMemo() {
        return vetoApprMemo;
    }

    public void setVetoApprMemo(boolean vetoApprMemo) {
        this.vetoApprMemo = vetoApprMemo;
    }
}