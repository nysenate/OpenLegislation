package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.bill.exception.ApprovalNotFoundException;
import gov.nysenate.openleg.legislation.bill.ApprovalId;
import gov.nysenate.openleg.legislation.bill.ApprovalMessage;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.util.List;

public interface ApprovalDataService
{
    /**
     * Retrieves a approval message with the given approval id
     * @param approvalId
     * @return
     * @throws ApprovalNotFoundException
     */
    ApprovalMessage getApprovalMessage(ApprovalId approvalId) throws ApprovalNotFoundException;

    /**
     * Retrieves a approval message for the given base bill
     * @param baseBillId
     * @return
     * @throws ApprovalNotFoundException
     */
    ApprovalMessage getApprovalMessage(BaseBillId baseBillId) throws ApprovalNotFoundException;

    /**
     * Retrieves all approval messages for a given year ordered by approval id number
     * @param year
     * @return
     * @throws ApprovalNotFoundException
     */
    List<ApprovalMessage> getApprovalMessages(int year) throws ApprovalNotFoundException;

    /**
     * Updates or inserts the given approval message
     * @param approvalMessage
     * @param legDataFragment
     */
    void updateApprovalMessage(ApprovalMessage approvalMessage, LegDataFragment legDataFragment);

    /**
     * Deletes any approval message with the given approval id
     * @param approvalId
     */
    void deleteApprovalMessage(ApprovalId approvalId);

    /**
     * Deletes any approval message that approves the bill designated by the given base bill id
     * @param baseBillId
     */
    void deleteApprovalMessage(BaseBillId baseBillId);
}