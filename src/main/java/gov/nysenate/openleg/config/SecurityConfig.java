package gov.nysenate.openleg.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
public class SecurityConfig
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired protected List<Realm> realms;

    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setFilterChainDefinitionMap(shiroIniConfig().getSection("urls"));
        return shiroFilter;
    }

    /**
     * Integrates Apache Shiro with Spring
     * @return LifecycleBeanPostProcessor
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * This is needed for Shiro annotations to work.
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
       AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
       advisor.setSecurityManager(securityManager());
       return advisor;
    }

    /**
     * Configures the shiro security manager with the instance of the active realm.
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setCacheManager(shiroCacheManager());
        return defaultWebSecurityManager;
    }

    @Bean(name = "shiroCacheManager")
    public CacheManager shiroCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * Exposes the shiro.ini configuration file as an Ini instance that is consumed by the
     * security filter manager when setting up the filter chains.
     */
    public Ini shiroIniConfig() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }
}