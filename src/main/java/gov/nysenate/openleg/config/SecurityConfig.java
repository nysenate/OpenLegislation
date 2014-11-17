package gov.nysenate.openleg.config;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;

@Configuration
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/");
        shiroFilter.setUnauthorizedUrl("/unauthorized");

        HashMap <String, String> properties = new HashMap<>();
        properties.put("/admin/**", "authcBasic");
        properties.put("/logout", "logout");

        shiroFilter.setFilterChainDefinitionMap(properties);
        return shiroFilter;
    }

    /**
     *
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn ("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor()
    {
       AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
       advisor.setSecurityManager(securityManager());
       return advisor;
    }

    /**
     * Configures the shiro security manager with the instance of the active realm.
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        return new DefaultWebSecurityManager(simple());
    }

    /**
     * Required for using Shiro annotations.
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthenticatingRealm simple() {
        SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
        simpleAccountRealm.addAccount("Admin", "test");
        return simpleAccountRealm;
    }
}