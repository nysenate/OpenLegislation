package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.dao.bill.data.VetoDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleVetoDataService implements VetoDataService
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleVetoDataService.class);

    @Autowired
    private VetoDao vetoDao;

    /** {@inheritDoc} */
    @Override
    public VetoMessage getVetoMessage(VetoId vetoId) throws VetoNotFoundException {
        if (vetoId==null) {
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
    public void updateVetoMessage(VetoMessage vetoMessage, SobiFragment sobiFragment) {
        vetoDao.updateVetoMessage(vetoMessage, sobiFragment);
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