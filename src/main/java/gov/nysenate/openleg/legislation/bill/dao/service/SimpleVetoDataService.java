package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.VetoId;
import gov.nysenate.openleg.legislation.bill.VetoMessage;
import gov.nysenate.openleg.legislation.bill.dao.VetoDao;
import gov.nysenate.openleg.legislation.bill.exception.VetoNotFoundException;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleVetoDataService implements VetoDataService {
    private final VetoDao vetoDao;

    @Autowired
    public SimpleVetoDataService(VetoDao vetoDao) {
        this.vetoDao = vetoDao;
    }

    /** {@inheritDoc} */
    @Override
    public VetoMessage getVetoMessage(VetoId vetoId) throws VetoNotFoundException {
        if (vetoId == null) {
            throw new IllegalArgumentException("vetoId cannot be null!");
        }
        try {
            return vetoDao.getVetoMessage(vetoId);
        }
        catch (EmptyResultDataAccessException ex){
            throw new VetoNotFoundException(ex, vetoId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws VetoNotFoundException {
        if (baseBillId == null) {
            throw new IllegalArgumentException("baseBillId cannot be null!");
        }
        try {
            return vetoDao.getBillVetoes(baseBillId);
        }
        catch (EmptyResultDataAccessException ex){
            throw new VetoNotFoundException(ex, baseBillId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateVetoMessage(VetoMessage vetoMessage, LegDataFragment legDataFragment) {
        vetoDao.updateVetoMessage(vetoMessage, legDataFragment);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteVetoMessage(VetoId vetoId) {
        vetoDao.deleteVetoMessage(vetoId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBillVetoes(BaseBillId baseBillId) {
        vetoDao.deleteBillVetoes(baseBillId);
    }
}
