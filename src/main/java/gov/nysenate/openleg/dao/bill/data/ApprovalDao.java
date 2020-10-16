package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface ApprovalDao {

    /**
     * Retrieves an approval message based on its year and approval number
     * @param approvalId
     * @return
     * @throws DataAccessException
     */
    ApprovalMessage getApprovalMessage(ApprovalId approvalId) throws DataAccessException;

    /**
     * Retrieves an approval message for a particular base bill
     * @param baseBillId
     * @return
     * @throws DataAccessException
     */
    ApprovalMessage getApprovalMessage(BaseBillId baseBillId) throws DataAccessException;

    /**
     * Gets all approval messages for the given year ordered by approval id number
     * @param year
     * @return
     * @throws DataAccessException
     */
    List<ApprovalMessage> getApprovalMessages(int year) throws DataAccessException;

    /**
     * Updates or inserts the given approval message into the persistence layer
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
