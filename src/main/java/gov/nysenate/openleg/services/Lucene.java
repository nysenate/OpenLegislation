package gov.nysenate.openleg.services;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;

public class Lucene extends ServiceBase
{
    @Override
    public boolean process(List<Entry<String, Change>> entries, Storage storage) throws IOException
    {
        gov.nysenate.openleg.lucene.Lucene lucene = Application.getLucene();
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
                    Class<? extends BaseObject> objType = classMap.get(otype);
                    BaseObject obj = storage.get(key, objType);
                    if (obj.isPublished()) {
                        Document document = null;

                        if (otype.equals("bill")) {
                            // Regenerate all the bill sub-documents. Delete them all first to
                            // account for any removals or changes in the document oid. Add back
                            // references so that the document can be properly constructed.
                            //
                            // TODO: Should we really be pulling the modified date from the bill?
                            Bill bill = (Bill)obj;

                            lucene.deleteDocumentsByQuery("otype:action AND billno:" + bill.getBillId());
                            for(Action billEvent:bill.getActions()) {
                                try {
                                    billEvent.setBill(bill);
                                    lucene.updateDocument(DocumentBuilder.build(billEvent));
                                }
                                catch (IOException e) {
                                    logger.error("Error indexing: "+key, e);
                                }
                            }

                            lucene.deleteDocumentsByQuery("otype:vote AND billno:" + bill.getBillId());
                            for(Vote vote: bill.getVotes()) {
                                try {
                                    vote.setBill(bill);
                                    lucene.updateDocument(DocumentBuilder.build(vote));
                                }
                                catch(IOException e) {
                                    logger.error("Error indexing: "+key, e);
                                }
                            }

                            document = DocumentBuilder.build(bill);
                        }
                        else if (otype.equals("meeting")) {
                            document = DocumentBuilder.build((Meeting)obj);
                        }
                        else if (otype.equals("calendar")) {
                            document = DocumentBuilder.build((Calendar)obj);
                        }
                        else if (otype.equals("transcript")) {
                            document = DocumentBuilder.build((Transcript)obj);
                        }
                        else if (otype.equals("hearing")) {
                            // Do nothing
                        }


                        if (document != null) {
                            try {
                                lucene.updateDocument(document);
                            }
                            catch (IOException e) {
                                logger.error("Error indexing: "+key, e);
                            }
                        }
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
