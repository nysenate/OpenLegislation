package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillQuery implements BasicSqlQuery
{
    /** --- Bill Base --- */

    SELECT_BILL(
        "SELECT * FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    SELECT_BILL_IDS_BY_SESSION(
        "SELECT print_no, session_year FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE session_year = :sessionYear"
    ),
    SELECT_COUNT_ALL_BILLS(
        "SELECT count(*) AS total FROM ${schema}." + SqlTable.BILL
    ),
    SELECT_COUNT_ALL_BILLS_IN_SESSION(
        SELECT_COUNT_ALL_BILLS.sql + " WHERE session_year = :sessionYear"
    ),
    UPDATE_BILL(
        "UPDATE ${schema}." + SqlTable.BILL + "\n" +
        "SET title = :title, summary = :summary, active_version = :activeVersion,\n" +
        "    active_year = :activeYear, program_info = :programInfo, program_info_num = :programInfoNum, status = :status, status_date = :statusDate,\n" +
        "    modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, last_fragment_id = :lastFragmentId\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    INSERT_BILL(
        "INSERT INTO ${schema}." + SqlTable.BILL + "\n" +
        "(print_no, session_year, title, summary, active_version, active_year, " +
        " program_info, program_info_num, status, status_date, modified_date_time, published_date_time, last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :title, :summary, :activeVersion, :activeYear, " +
        "        :programInfo, :programInfoNum, :status, :statusDate, :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
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

    /** --- Bill Amendment --- */

    SELECT_BILL_AMENDMENTS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMENDMENT(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "SET sponsor_memo = :sponsorMemo, act_clause = :actClause, full_text = :fullText, stricken = :stricken, " +
        "    current_committee_name = :currentCommitteeName, current_committee_action = :currentCommitteeAction, " +
        "    uni_bill = :uniBill, last_fragment_id = :lastFragmentId, law_section = :lawSection, law_code = :lawCode\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND version = :version"
    ),
    INSERT_BILL_AMENDMENT(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "(bill_print_no, bill_session_year, version, sponsor_memo, act_clause, full_text, stricken, " +
        " current_committee_name, current_committee_action, uni_bill, last_fragment_id, law_section, law_code)\n" +
        "VALUES(:printNo, :sessionYear, :version, :sponsorMemo, :actClause, :fullText, :stricken, " +
        "       :currentCommitteeName, :currentCommitteeAction, :uniBill, :lastFragmentId, :lawSection, :lawCode)"
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
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo"
    ),
    DELETE_BILL_VOTES_INFO(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo"
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