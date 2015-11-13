package gov.nysenate.openleg.service.auth;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AuthRealmConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(AuthRealmConfigurer.class);

    @Autowired protected List<Realm> realmList;
    @Autowired protected DefaultWebSecurityManager securityManager;

    @PostConstruct
    public void setUp() {
        securityManager.setRealms(realmList);
    }
}
