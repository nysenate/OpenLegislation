package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillQuery implements BasicSqlQuery
{
    /** --- Bill Base --- */

    SELECT_BILL(
        "SELECT * FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    SELECT_BILL_IDS_BY_SESSION(
        "SELECT bill_print_no, bill_session_year FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE bill_session_year = :sessionYear"
    ),
    SELECT_COUNT_ALL_BILLS(
        "SELECT count(*) AS total FROM ${schema}." + SqlTable.BILL
    ),
    SELECT_COUNT_ALL_BILLS_IN_SESSION(
        SELECT_COUNT_ALL_BILLS.sql + " WHERE bill_session_year = :sessionYear"
    ),
    UPDATE_BILL(
        "UPDATE ${schema}." + SqlTable.BILL + "\n" +
        "SET title = :title, summary = :summary, active_version = :activeVersion, sub_bill_print_no = :subPrintNo,\n" +
        "    active_year = :activeYear, program_info = :programInfo, program_info_num = :programInfoNum, " +
        "    status = :status, status_date = :statusDate, committee_name = :committeeName, " +
        "    committee_chamber = :committeeChamber::chamber, bill_cal_no = :billCalNo, " +
        "    modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, last_fragment_id = :lastFragmentId\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL(
        "INSERT INTO ${schema}." + SqlTable.BILL + "\n" +
        "(bill_print_no, bill_session_year, title, summary, active_version, active_year, sub_bill_print_no, " +
        " program_info, program_info_num, status, status_date, committee_name, committee_chamber, bill_cal_no, " +
        " modified_date_time, published_date_time, last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :title, :summary, :activeVersion, :activeYear, :subPrintNo, " +
        "        :programInfo, :programInfoNum, :status, :statusDate, :committeeName, :committeeChamber::chamber, :billCalNo, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),
    ACTIVE_SESSION_YEARS(
        "SELECT min(bill_session_year) as min, max(bill_session_year) as max\n" +
        "FROM ${schema}." + SqlTable.BILL
    ),

    /** --- Bill Sponsor --- */

    SELECT_BILL_SPONSOR(
        "SELECT * FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_SPONSOR(
        "INSERT INTO ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "(bill_print_no, bill_session_year, session_member_id, budget_bill, rules_sponsor, last_fragment_id) " +
        "VALUES (:printNo, :sessionYear, :sessionMemberId, :budgetBill, :rulesSponsor, :lastFragmentId)"
    ),
    UPDATE_BILL_SPONSOR(
        "UPDATE ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "SET session_member_id = :sessionMemberId, budget_bill = :budgetBill, rules_sponsor = :rulesSponsor, " +
        "last_fragment_id = :lastFragmentId\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    DELETE_BILL_SPONSOR(
        "DELETE FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Addtional Bill Sponsors --- */

    SELECT_ADDTL_BILL_SPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_ADDITIONAL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Bill Text --- */

    SELECT_BILL_TEXT(
        "SELECT bill_print_no, bill_session_year, bill_amend_version, sponsor_memo, full_text \n" +
        "FROM ${schema}.bill_amendment \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    SELECT_ALTERNATE_PDF_URL(
        "SELECT url_path \n" +
        "FROM ${schema}." + SqlTable.BILL_ALTERNATE_PDF + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND active = true"
    ),

    /** --- Bill Amendment --- */

    SELECT_BILL_AMENDMENTS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMENDMENT(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "SET sponsor_memo = :sponsorMemo, act_clause = :actClause, full_text = :fullText, stricken = :stricken, " +
        "    uni_bill = :uniBill, last_fragment_id = :lastFragmentId, law_section = :lawSection, law_code = :lawCode\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_AMENDMENT(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, sponsor_memo, act_clause, full_text, stricken, " +
        " uni_bill, last_fragment_id, law_section, law_code)\n" +
        "VALUES(:printNo, :sessionYear, :version, :sponsorMemo, :actClause, :fullText, :stricken, " +
        "       :uniBill, :lastFragmentId, :lawSection, :lawCode)"
    ),

    /** --- Bill Amendment Publish Status --- */

    SELECT_BILL_AMEND_PUBLISH_STATUSES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_PUBLISH_STATUS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMEND_PUBLISH_STATUS(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT_PUBLISH_STATUS + "\n" +
        "SET published = :published, effect_date_time = :effectDateTime, override = :override, notes = :notes," +
        "    last_fragment_id = :lastFragmentId\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_AMEND_PUBLISH_STATUS(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_PUBLISH_STATUS + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, published, effect_date_time, override, notes, " +
        " last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :version, :published, :effectDateTime, :override, :notes, :lastFragmentId)"
    ),

    /** --- Bill Amendment Cosponsors --- */

    SELECT_BILL_COSPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_COSPONSOR(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, session_member_id, sequence_no, last_fragment_id)\n" +
        "VALUES (:printNo, :sessionYear, :version, :sessionMemberId, :sequenceNo, :lastFragmentId)"
    ),
    UPDATE_BILL_COSPONSOR(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + " " +
        "SET sequence_no = :sequenceNo, last_fragment_id = :lastFragmentId\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "      AND session_member_id = :sessionMemberId"
    ),
    DELETE_BILL_COSPONSORS(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    DELETE_BILL_COSPONSOR(
        DELETE_BILL_COSPONSORS.sql + " AND session_member_id = :sessionMemberId"
    ),

    /** --- Bill Amendment Multi-sponsors --- */

    SELECT_BILL_MULTISPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_MULTISPONSOR(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, session_member_id, sequence_no, last_fragment_id)\n" +
        "VALUES (:printNo, :sessionYear, :version, :sessionMemberId, :sequenceNo, :lastFragmentId)"
    ),
    UPDATE_BILL_MULTISPONSOR(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + " " +
        "SET sequence_no = :sequenceNo, last_fragment_id = :lastFragmentId\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "      AND session_member_id = :sessionMemberId"
    ),
    DELETE_BILL_MULTISPONSORS(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    DELETE_BILL_MULTISPONSOR(
        DELETE_BILL_MULTISPONSORS.sql + " AND session_member_id = :sessionMemberId"
    ),

    /** --- Bill Amendment Votes --- */

    SELECT_BILL_VOTES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " info \n" +
        "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + " roll ON info.id = roll.vote_id\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_VOTES_INFO(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, vote_type, vote_date, sequence_no, " +
        " committee_name, committee_chamber, modified_date_time, published_date_time, last_fragment_id) " +
        "VALUES (:printNo, :sessionYear, :version, :voteType::${schema}.vote_type, :voteDate, :sequenceNo, " +
        "        :committeeName, :committeeChamber::chamber, :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),
    INSERT_BILL_VOTES_ROLL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + "\n" +
        "(vote_id, vote_code, session_member_id, member_short_name, session_year, last_fragment_id)\n" +
        "SELECT id, :voteCode::${schema}.vote_code, :sessionMemberId, :memberShortName, :sessionYear, :lastFragmentId " +
        "FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo\n" +
        "AND COALESCE(committee_name, '') = COALESCE(:committeeName, '')"
    ),
    DELETE_BILL_VOTES_INFO(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo \n" +
        "AND COALESCE(committee_name, '') = COALESCE(:committeeName, '')"
    ),

    /** --- Bill Actions --- */

    SELECT_BILL_ACTIONS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear "
    ),
    INSERT_BILL_ACTION(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, effect_date, chamber, text, sequence_no, " +
        " last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :version, :effectDate, CAST(:chamber as chamber), :text, :sequenceNo, " +
        "        :lastFragmentId)"
    ),
    DELETE_BILL_ACTION("" +
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version \n" +
        "      AND sequence_no = :sequenceNo"),

    /** --- Bill Same As --- */

    SELECT_BILL_SAME_AS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_SAME_AS(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, same_as_bill_print_no, same_as_session_year, " +
        " same_as_amend_version, last_fragment_id)\n" +
        "VALUES (:printNo, :sessionYear, :version, :sameAsPrintNo, :sameAsSessionYear, :sameAsVersion, :lastFragmentId)"
    ),
    DELETE_SAME_AS_FOR_BILL(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    DELETE_SAME_AS(
        DELETE_SAME_AS_FOR_BILL.sql + " AND same_as_bill_print_no = :sameAsPrintNo AND " +
        "same_as_session_year = :sameAsSessionYear AND same_as_amend_version = :sameAsVersion"
    ),

    /** --- Bill Committee --- */

    SELECT_BILL_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "(bill_print_no, bill_session_year, committee_name, committee_chamber, action_date, last_fragment_id)" + "\n" +
        "VALUES ( :printNo, :sessionYear, :committeeName, :committeeChamber::chamber, :actionDate, :lastFragmentId)"
    ),
    DELETE_BILL_COMMITTEES(
        "DELETE FROM ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    DELETE_BILL_COMMITTEE(
        DELETE_BILL_COMMITTEES.sql + " AND committee_name = :committeeName AND \n" +
        "committee_chamber = :committeeChamber::chamber AND action_date = :actionDate"
    ),

    /** --- Bill Previous Version --- */

    SELECT_BILL_PREVIOUS_VERSIONS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    SELECT_ALL_BILL_PREVIOUS_VERSIONS(
        "WITH RECURSIVE prev_version(bill_id, amend_version, session_year) AS ( \n" +
        "    SELECT prev_bill_print_no, prev_amend_version, prev_bill_session_year \n" +
        "    FROM ${schema}.bill_previous_version \n" +
        "    WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear \n" +
        "" +
        "    UNION \n" +
        "    SELECT prev_bill_print_no, prev_amend_version, prev_bill_session_year \n" +
        "    FROM prev_version, ${schema}.bill_previous_version \n" +
        "    WHERE bill_print_no = bill_id AND bill_session_year = session_year) \n" +
        "SELECT bill_id AS prev_bill_print_no, amend_version AS prev_amend_version, " +
        "       session_year AS prev_bill_session_year \n" +
        "FROM prev_version"
    ),
    INSERT_BILL_PREVIOUS_VERSION(
        "INSERT INTO ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "(bill_print_no, bill_session_year, prev_bill_print_no, prev_bill_session_year, prev_amend_version, " +
        " last_fragment_id)\n" +
        "VALUES (:printNo, :sessionYear, :prevPrintNo, :prevSessionYear, :prevVersion, :lastFragmentId)"
    ),
    DELETE_BILL_PREVIOUS_VERSIONS(
        "DELETE FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    DELETE_BILL_PREVIOUS_VERSION(
        DELETE_BILL_PREVIOUS_VERSIONS.sql + " AND prev_bill_print_no = :prevPrintNo AND " +
        "prev_bill_session_year = :prevSessionYear AND prev_amend_version = :prevVersion"
    ),

    /** --- Bill Milestones --- */

    GET_BILL_MILESTONES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_MILESTONE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_MILESTONE("" +
        "INSERT INTO ${schema}." + SqlTable.BILL_MILESTONE + "\n" +
        "(bill_print_no, bill_session_year, status, rank, action_sequence_no, date, committee_name, committee_chamber," +
        " cal_no, last_fragment_id)\n" +
        "VALUES (:printNo, :sessionYear, :status, :rank, :actionSequenceNo, :date, :committeeName, :committeeChamber::chamber," +
        "        :calNo, :lastFragmentId)"
    ),
    DELETE_BILL_MILESTONES("" +
        "DELETE FROM ${schema}." + SqlTable.BILL_MILESTONE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Associated Committee Agenda Ids --- */

    SELECT_COMM_AGENDA_IDS(
        "SELECT aic.agenda_no, aic.year, aic.committee_name \n" +
        "FROM ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE_ITEM + " aici\n" +
        "JOIN ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE + " aic ON aici.info_committee_id = aic.id\n" +
        "WHERE aici.bill_print_no = :printNo AND aici.bill_session_year = :sessionYear"
    ),

    /** --- Associated Calendar Ids -- */

    SELECT_CALENDAR_IDS(
        "SELECT cs.calendar_no, cs.calendar_year \n" +
        "FROM ${schema}." + SqlTable.CALENDAR_SUP_ENTRY + " cse\n" +
        "JOIN ${schema}." + SqlTable.CALENDAR_SUPPLEMENTAL + " cs ON cse.calendar_sup_id = cs.id\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    );

    private String sql;

    SqlBillQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}