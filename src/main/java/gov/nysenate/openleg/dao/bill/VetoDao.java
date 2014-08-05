package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.bill.VetoId;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface VetoDao {

    /**
     * Get a specific veto via its veto number
     *
     * @param vetoId
     * @return VetoMessage
     * @throws DataAccessException if the veto cannot be found
     */
    public VetoMessage getVetoMessage(VetoId vetoId) throws DataAccessException;

    /**
     * Retrieves a chronologically ordered list of vetoes corresponding to the given bill
     *
     * @param baseBillId
     * @return List<VetoMessage>
     * @throws DataAccessException if no vetoes are found
     */
    public List<VetoMessage> getBillVetoes(BaseBillId baseBillId) throws DataAccessException;

    /**
     * Updates or inserts the given vetoMessage
     *
     * @param vetoMessage
     * @throws DataAccessException if there is an error inserting/updating the vetoMessage
     */
    public void updateVetoMessage(VetoMessage vetoMessage) throws DataAccessException;

}
