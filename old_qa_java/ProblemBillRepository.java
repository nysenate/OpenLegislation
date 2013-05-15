package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.ProblemBill;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;

@View(	name = "all",
map  = "function(doc) { if (doc.oid) { emit(doc.oid, doc) } }")
public class ProblemBillRepository extends CouchDbRepositorySupport<ProblemBill> {

    private final Logger logger = Logger.getLogger(ProblemBillRepository.class);

    public static final Class<ProblemBill> clazz = ProblemBill.class;

    public ProblemBillRepository(CouchDbConnector db) {
        super(clazz, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public ProblemBill findByOid(String oid) {
        List<ProblemBill> problemBills = queryView("by_oid", oid);
        if(problemBills == null || problemBills.isEmpty())
            return null;

        return problemBills.get(0);
    }

    @View(	name = "problem_bills_by_modified",
            map  = "function(doc) { if(doc.oid) { emit(doc.modified, doc) } }")
    public List<ProblemBill> findByModified(boolean descending) {
        return db.queryView(
                createQuery("problem_bills_by_modified")
                .includeDocs(true).descending(descending), clazz);
    }

    @View(	name = "problem_bills_by_rank",
            map  = "function(doc) { if(doc.oid && doc.rank) { emit(doc.rank, doc) } }")
    public List<ProblemBill> findProblemBillsByRank() {
        return db.queryView(
                createQuery("problem_bills_by_rank")
                .includeDocs(true).descending(true), clazz);
    }

    @View(	name = "problem_bills_to_delete",
            map  = "function(doc) { " +
                    "if(doc.oid && !doc.nonMatchingFields && !doc.missingFields) {" +
            "emit(doc.oid, doc) } }")
    public List<ProblemBill> findProblemBillsToDelete() {
        return db.queryView(
                createQuery("problem_bills_to_delete")
                .includeDocs(true), clazz);
    }

    @View(	name = "problem_bills_by_missing_fields",
            map  = "function(doc) { if(doc.oid && doc.missingFields) { emit(doc.oid, doc) } }")
    public List<ProblemBill> findByMissingFields() {
        return db.queryView(
                createQuery("problem_bills_by_missing_fields")
                .includeDocs(true), clazz);
    }


    public void createOrUpdateProblemBill(ProblemBill problemBill, boolean merge) {
        createOrUpdateProblemBill(problemBill, merge, new FieldName[0]);
    }

    public void createOrUpdateProblemBills(Collection<ProblemBill> problemBills, boolean merge) {
        createOrUpdateProblemBills(problemBills, merge, new FieldName[0]);
    }

    public void createOrUpdateProblemBills(Collection<ProblemBill> problemBills, boolean merge, FieldName[] fieldNames) {
        for(ProblemBill problemBill:problemBills) {
            createOrUpdateProblemBill(problemBill, merge, fieldNames);
        }
    }

    public void createOrUpdateProblemBill(ProblemBill problemBill, boolean merge, FieldName[] fieldNames) {
        logger.info("Updating " + problemBill.getOid() + ", merging: " + merge);

        ProblemBill temp = findByOid(problemBill.getOid());
        if(temp == null) {
            db.create(problemBill);
        }
        else {
            problemBill.setRevision(temp.getRevision());

            //before merge we want to remove existing fields from previous reports
            temp = ProblemBill.removeNonMatchingFields(temp, fieldNames);

            db.update(merge ? ProblemBill.merge(problemBill, temp) : problemBill);
        }
    }

    public void deleteNonProblemBills() {
        List<ProblemBill> problemBills = findProblemBillsToDelete();
        for(ProblemBill problemBill:problemBills) {
            db.delete(problemBill);
        }
    }

    public void rankProblemBills() {
        List<ProblemBill> problemBills = findByModified(true);

        if(problemBills == null || problemBills.isEmpty())
            return;

        long newestMod = problemBills.get(0).getModified();

        for(ProblemBill problemBill:problemBills) {
            problemBill.setRank(
                    ProblemBill.rank(
                            newestMod,
                            problemBill.getModified(),
                            (problemBill.getNonMatchingFields() != null
                            ? problemBill.getNonMatchingFields().size() : 0)
                            + (problemBill.getMissingFields() != null
                            ? problemBill.getMissingFields().size() : 0)));
            update(problemBill);
        }
    }
}