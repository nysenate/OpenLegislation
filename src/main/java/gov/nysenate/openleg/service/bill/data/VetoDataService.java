package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.Map;

public interface VetoDataService
{
    /**
     * Get a specific veto via its veto number
     *
     * @param vetoId
     * @return VetoMessage
     * @throws VetoNotFoundException if the veto cannot be found
     */
    public VetoMessage getVetoMessage(VetoId vetoId) throws VetoNotFoundException;

    /**
     * Retrieves a chronologically ordered list of vetoes corresponding to the given bill
     *
     * @param baseBillId
     * @return List<VetoMessage>
     * @throws VetoNotFoundException if no vetoes are found
     */
    public Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws VetoNotFoundException;

    /**
     * Updates or inserts the given vetoMessage
     *
     * @param sobiFragment
     * @param vetoMessage
     */
    public void updateVetoMessage(VetoMessage vetoMessage, SobiFragment sobiFragment);

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
