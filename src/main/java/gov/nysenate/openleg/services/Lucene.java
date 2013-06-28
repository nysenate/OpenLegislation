package gov.nysenate.openleg.services;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Change;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

public class Lucene extends ServiceBase
{
    private final gov.nysenate.openleg.lucene.Lucene lucene;

    public Lucene(String indexDir) throws IOException
    {
        super();
        lucene = new gov.nysenate.openleg.lucene.Lucene(new File(indexDir));
    }

    @Override
    public boolean process(List<Entry<String, Change>> entries, Storage storage) throws IOException
    {
        for(Entry<String, Change> entry : entries) {
            try {
                // Extract object descriptors from the entry key
                String key = entry.getKey();
                String otype = key.split("/")[1];
                String oid = key.split("/")[2];
                Change change = entry.getValue();
                logger.debug("Indexing "+change.getStatus()+" "+otype+": "+oid);

                // We don't currently index agendas; meetings get their own entries
                // and meeting votes are attached to bills
                if (otype.equals("agenda")) continue;

                if (change.getStatus() == Storage.Status.DELETED) {
                    if(otype.equals("bill")) {
                        // When a bill is deleted, also remove its sub-documents
                        lucene.deleteDocumentsByQuery("otype:action AND billno:" + oid);
                        lucene.deleteDocumentsByQuery("otype:vote AND billno:" + oid);
                        lucene.deleteDocumentById(oid);
                    }
                    else {
                        // All sub-documents of other types have their own entries in the
                        // change log and will delete themselves.
                        lucene.deleteDocumentById(oid);
                    }
                }
                else {
                    // Retrieve new/modified objects from storage so we can index them
                    Class<? extends ILuceneObject> objType = classMap.get(otype);
                    ILuceneObject obj = (ILuceneObject) storage.get(key, objType);

                    if (otype.equals("bill")) {
                        // Regenerate all the bill sub-documents. Delete them all first to
                        // account for any removals or changes in the document oid. Add back
                        // references so that the document can be properly constructed.
                        //
                        // TODO: Should we really be pulling the modified date from the bill?
                        Bill bill = (Bill)obj;

                        lucene.deleteDocumentsByQuery("otype:action AND billno:" + bill.getSenateBillNo());
                        for(Action be:bill.getActions()) {
                            try {
                                be.setModified(bill.getModified());
                                be.setBill(bill);
                                lucene.updateDocument(be);
                            }
                            catch (IOException e) {
                                logger.error("Error indexing: "+be.luceneOid(), e);
                            }
                        }

                        lucene.deleteDocumentsByQuery("otype:vote AND billno:" + bill.getSenateBillNo());
                        for(Vote vote: bill.getVotes()) {
                            try {
                                vote.setModified(bill.getModified());
                                vote.setBill(bill);
                                lucene.updateDocument(vote);
                            }
                            catch(IOException e) {
                                logger.error("Error indexing: "+vote.luceneOid(), e);
                            }
                        }
                    }

                    try {
                        lucene.updateDocument(obj);
                    }
                    catch (IOException e) {
                        logger.error("Error indexing: "+obj.luceneOid(), e);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                logger.error("Unable to parse oid and otype values from key: "+entry.getKey(), e);
            }
            catch (Exception e) {
                logger.error("Unexpected error processing entry: "+entry.getKey(), e);
            }
        }

        lucene.commit();
        logger.info("done indexing objects(" + entries.size() + "). Closing index.");
        return true;
    }
}
