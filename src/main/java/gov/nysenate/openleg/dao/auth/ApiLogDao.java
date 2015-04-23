package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface ApiLogDao
{
    public int saveApiRequest(ApiRequest req) throws DataAccessException;

    public void saveApiResponse(ApiResponse res) throws DataAccessException;

    public List<ApiRequest> getRequests ();
}
