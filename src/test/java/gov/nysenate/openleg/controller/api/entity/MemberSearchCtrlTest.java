package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.controller.api.ApiTest;
import gov.nysenate.openleg.model.entity.SessionMember;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(UnitTest.class)
public class MemberSearchCtrlTest extends ApiTest {
    @Autowired
    private MemberSearchCtrl testCtrl;

    private SessionMember searchBySessionMemberId() {
        return null;
    }
}
