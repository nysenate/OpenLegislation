package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.dao.bill.data.ApprovalDao;
import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleApprovalDataService implements ApprovalDataService
{
    public static final Logger logger = LoggerFactory.getLogger(SimpleApprovalDataService.class);

    @Autowired
    private ApprovalDao approvalDao;

    /** {@inheritDoc} */
    @Override
    public ApprovalMessage getApprovalMessage(ApprovalId approvalId) throws ApprovalNotFoundException {
        try {
            return approvalDao.getApprovalMessage(approvalId);
        }
        catch(DataAccessException ex) {
            throw new ApprovalNotFoundException(ex, approvalId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ApprovalMessage getApprovalMessage(BaseBillId baseBillId) throws ApprovalNotFoundException {
        try {
            return approvalDao.getApprovalMessage(baseBillId);
        }
        catch(DataAccessException ex) {
            throw new ApprovalNotFoundException(ex, baseBillId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ApprovalMessage> getApprovalMessages(int year) throws ApprovalNotFoundException {
        try {
            return approvalDao.getApprovalMessages(year);
        }
        catch(DataAccessException ex){
            throw new ApprovalNotFoundException(ex, year);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateApprovalMessage(ApprovalMessage approvalMessage, SobiFragment sobiFragment) {
        approvalDao.updateApprovalMessage(approvalMessage, sobiFragment);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApprovalMessage(ApprovalId approvalId) {
        approvalDao.deleteApprovalMessage(approvalId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApprovalMessage(BaseBillId baseBillId) {
        approvalDao.deleteApprovalMessage(baseBillId);
    }
}