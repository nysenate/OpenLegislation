package gov.nysenate.openleg.dao.log;

import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface ApiLogDao
{
    int saveApiRequest(ApiRequest req) throws DataAccessException;

    void saveApiResponse(ApiResponse res) throws DataAccessException;

    List<ApiRequest> getRequests ();
}
