package gov.nysenate.openleg.search.committee;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeView;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class ElasticCommitteeSearchDao extends ElasticBaseDao<CommitteeVersionId, CommitteeView, Committee> {
    @Override
    public SearchIndex indexType() {
        return SearchIndex.COMMITTEE;
    }

    @Override
    protected String getId(Committee data) {
        return data.getVersionId().toString();
    }

    @Override
    protected CommitteeView getDoc(Committee data) {
        return new CommitteeView(data);
    }

    @Override
    protected CommitteeVersionId toId(String idStr) {
        String[] parts = idStr.split("-");
        return new CommitteeVersionId(Chamber.getValue(parts[0]), parts[1],
                SessionYear.of(Integer.parseInt(parts[2])), LocalDateTime.parse(parts[3]));
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("meetTime", basicTimeMapping);
    }
}
