package gov.nysenate.openleg.dao.transcript.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao implements TranscriptSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticTranscriptSearchDao.class);

    protected static final String transcriptIndexName = SearchIndex.TRANSCRIPT.getIndexName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(QueryBuilder query, FilterBuilder filter, String sort, LimitOffset limOff) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Transcript transcript) {
        updateTranscriptIndex(Arrays.asList(transcript));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Collection<Transcript> transcripts) {
        if (!transcripts.isEmpty()) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<TranscriptView> transcriptViewList = transcripts.stream().map(TranscriptView::new).collect(Collectors.toList());
            transcriptViewList.forEach(t ->
                            bulkRequest.add(
                                    searchClient.prepareIndex(transcriptIndexName, t.getSessionType(), t.getDateTime().toString())
                                            .setSource(OutputUtils.toJson(t)))
            );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTranscriptFromIndex(TranscriptId transcriptId) {
        if (transcriptId != null) {
            deleteEntry(transcriptIndexName, transcriptId.getSessionType(), transcriptId.getDateTime().toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(transcriptIndexName);
    }
}
