package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.VetoId;
import gov.nysenate.openleg.legislation.bill.VetoMessage;
import gov.nysenate.openleg.legislation.bill.exception.VetoNotFoundException;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.util.Map;

public interface VetoDataService {
    /**
     * Get a specific veto via its veto number
     *
     * @param vetoId
     * @return VetoMessage
     * @throws VetoNotFoundException if the veto cannot be found
     */
    VetoMessage getVetoMessage(VetoId vetoId) throws VetoNotFoundException;

    /**
     * Retrieves a chronologically ordered list of vetoes corresponding to the given bill
     *
     * @param baseBillId
     * @return List<VetoMessage>
     * @throws VetoNotFoundException if no vetoes are found
     */
    Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws VetoNotFoundException;

    /**
     * Updates or inserts the given vetoMessage
     *
     * @param legDataFragment
     * @param vetoMessage
     */
    void updateVetoMessage(VetoMessage vetoMessage, LegDataFragment legDataFragment);

    /**
     * Deletes a veto message specified by the given veto id
     * @param vetoId
     */
    void deleteVetoMessage(VetoId vetoId);

    /**
     * Deletes all veto messages for the bill designated by the given base bill id
     * @param baseBillId
     */
    void deleteBillVetoes(BaseBillId baseBillId);
}
