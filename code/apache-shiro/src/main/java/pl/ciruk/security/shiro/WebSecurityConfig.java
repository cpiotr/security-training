package pl.ciruk.security.shiro;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSecurityConfig {

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public AuthorizingRealm ldapRealm() {
        AuthorizationLdapRealm realm = new AuthorizationLdapRealm();

        JndiLdapContextFactory ldapContextFactory = new JndiLdapContextFactory();
//        ldapContextFactory.setUrl("ldap://ldap.cern.ch:389");
        ldapContextFactory.setUrl("ldap://192.168.100.100:389");
        ldapContextFactory.setSystemUsername("cn=admin,dc=cern,dc=ch");
        ldapContextFactory.setSystemPassword("admin");

        realm.setContextFactory(ldapContextFactory);

        realm.setUserDnTemplate("uid={0},ou=people,dc=cern,dc=ch");

        realm.init();

        return realm;
    }

    @Bean
    public WebSecurityManager securityManager(AuthorizingRealm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    @Bean
    public AbstractShiroFilter shiroFilter(WebSecurityManager securityManager) throws Exception {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setFilters(createFilters());
        shiroFilter.setFilterChainDefinitionMap(createFilterChainMapping());
        return (AbstractShiroFilter) shiroFilter.getObject();
    }

    private Map<String, Filter> createFilters() {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("anon", new AnonymousFilter());
        filters.put("authc", new FormAuthenticationFilter());
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setRedirectUrl("/login?logout");
        filters.put("logout", logoutFilter);
        filters.put("roles", new RolesAuthorizationFilter());
        filters.put("user", new UserFilter());
        return filters;
    }

    private Map<String, String> createFilterChainMapping() {
        Map<String, String> filterChainDefinitionMapping = new HashMap<>();
        filterChainDefinitionMapping.put("/secured/*", "authc,roles[manager]");//,roles[guest],ssl[8443]
        filterChainDefinitionMapping.put("/standard/*", "authc");//,roles[guest],ssl[8443]
        filterChainDefinitionMapping.put("/login", "authc");
        filterChainDefinitionMapping.put("/logout", "logout");
        return filterChainDefinitionMapping;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        sourceAdvisor.setSecurityManager(securityManager);
        return sourceAdvisor;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }
}