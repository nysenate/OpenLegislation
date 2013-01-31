package gov.nysenate.openleg.services;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.serialize.JsonSerializer;
import gov.nysenate.openleg.util.serialize.XmlSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexWriter;

public class Lucene extends ServiceBase {
    public LuceneSerializer[] serializers;
    private final gov.nysenate.openleg.lucene.Lucene lucene;

    public Lucene(String indexDir) {
        super();
        lucene = new gov.nysenate.openleg.lucene.Lucene(indexDir);
        serializers = new LuceneSerializer[]{ new XmlSerializer(), new JsonSerializer()};
    }

    @Override
    public boolean process(HashMap<String, Storage.Status> changeLog, Storage storage) throws IOException {
        // Verify that an index exists
        lucene.createIndex();

        // Get a new writer
        IndexWriter indexWriter = lucene.newIndexWriter();

        for(Entry<String, Storage.Status> entry : changeLog.entrySet()) {
            try {
                logger.debug("Indexing "+entry.getValue()+": "+entry.getKey());
                String key = entry.getKey();
                String otype = key.split("/")[1];
                String oid = key.split("/")[2];
                logger.debug(otype+", "+oid);
                if( entry.getValue() == Storage.Status.DELETED ) {
                    if(otype.equals("bill")) {
                        lucene.deleteDocumentsByQuery("otype:action AND billno:" + oid, indexWriter);
                        lucene.deleteDocumentsByQuery("otype:vote AND billno:" + oid, indexWriter);;
                        lucene.deleteDocuments(otype, oid, indexWriter);

                    } else {
                        // XML sub-documents should cleaned up by their respective log entries
                        lucene.deleteDocuments(otype, oid, indexWriter);
                    }

                } else {
                    Class<? extends ILuceneObject> objType = classMap.get(otype);
                    ILuceneObject obj = (ILuceneObject) storage.get(key, objType);

                    if (otype.equals("bill")) {
                        Bill bill = (Bill)obj;

                        // Regenerate all the bill actions
                        lucene.deleteDocumentsByQuery("otype:action AND billno:" + bill.getSenateBillNo(), indexWriter);
                        if(bill.getActions() != null) {
                            for(Action be:bill.getActions()) {
                                try {
                                    be.setModified(bill.getModified());
                                    be.setBill(bill);
                                    lucene.addDocument(be, serializers, indexWriter);
                                } catch (Exception e) {
                                    // TODO: Something
                                    logger.error("Error indexing: "+be.luceneOid(), e);
                                }
                            }
                        }

                        // Regenerate all the bill votes
                        lucene.deleteDocumentsByQuery("otype:vote AND billno:" + bill.getSenateBillNo(), indexWriter);
                        if(bill.getVotes() != null) {
                            for(Vote vote: bill.getVotes()) {
                                try {
                                    vote.setModified(bill.getModified());
                                    vote.setBill(bill);
                                    lucene.addDocument(vote, serializers, indexWriter);
                                } catch(Exception e) {
                                    //TODO: Something
                                    logger.error("Error indexing: "+vote.luceneOid(), e);
                                }
                            }
                        }

                        try {
                            lucene.addDocument(bill, serializers, indexWriter);
                        } catch (Exception e) {
                            logger.error("Error indexing: "+bill.luceneOid(), e);
                        }

                    } else if(!otype.equals("agenda")){
                        try {
                            lucene.addDocument(obj, serializers, indexWriter);
                        } catch (Exception e) {
                            logger.error("Error indexing: "+obj.luceneOid(), e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing entry: "+entry.getKey(), e);
            }
        }

        indexWriter.commit();

        logger.info("done indexing objects(" + changeLog.size() + "). Closing index.");
        indexWriter.close();
        return true;
    }
}
