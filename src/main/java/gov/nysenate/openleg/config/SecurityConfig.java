package gov.nysenate.openleg.config;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class SecurityConfig {
    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setFilterChainDefinitionMap(Ini.fromResourcePath("classpath:shiro.ini")
                .getSection("urls"));
        shiroFilter.setLoginUrl("/admin/login");
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
     * Configures the security manager with the instance of the active realm.
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setCacheManager(new MemoryConstrainedCacheManager());
        return defaultWebSecurityManager;
    }
}
