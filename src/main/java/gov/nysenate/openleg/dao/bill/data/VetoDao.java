package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

import java.util.Map;

public interface VetoDao
{
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
    public Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws DataAccessException;

    /**
     * Updates or inserts the given vetoMessage
     *
     * @param vetoMessage
     * @param sobiFragment
     * @throws DataAccessException if there is an error inserting/updating the vetoMessage
     */
    public void updateVetoMessage(VetoMessage vetoMessage, SobiFragment sobiFragment) throws DataAccessException;

    /**
     * Deletes a veto message specified by the given veto id
     * @param vetoId
     */
    public void deleteVetoMessage(VetoId vetoId);

    /**
     * Deletes all veto messages for the bill designated by the given base bill id
     * @param baseBillId
     */
    public void deleteBillVetoes(BaseBillId baseBillId);

}
