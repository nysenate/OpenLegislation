package gov.nysenate.openleg.config;

import gov.nysenate.openleg.api.BaseCtrl;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;

import static javax.servlet.DispatcherType.*;

/**
 * Basically the web.xml in programmatic form.
 */
public class WebInitializer implements WebApplicationInitializer {
    protected static String DISPATCHER_SERVLET_NAME = "legislation";

    /**
     * Bootstraps the web application. Automatically invoked by Spring during startup.
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        /* Create the root Spring application context. */
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

        /* Manage the lifecycle of the root application context. */
        servletContext.addListener(new ContextLoaderListener(rootContext));

        /* The dispatcher servlet has it's own application context in which it can override
         * beans from the parent root context. */
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.setServletContext(servletContext);
        dispatcherContext.setParent(rootContext);
        dispatcherContext.register(WebApplicationConfig.class);

        /* Register the dispatcher servlet which basically serves as the front controller for Spring.
         * The servlet has to be mapped to the root path "/". */
        ServletRegistration.Dynamic dispatcher;
        dispatcher = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        dispatcher.setAsyncSupported(true);

        /* Encoding filter - sets Content-Type charset to UTF-8 */
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("encodingFilter", encodingFilter)
                .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, "/*");

        /* Register Apache Shiro */
        DelegatingFilterProxy shiroFilter = new DelegatingFilterProxy("shiroFilter", dispatcherContext);
        shiroFilter.setTargetFilterLifecycle(true);
        servletContext.addFilter("shiroFilter", shiroFilter)
            .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, "/*");

        /* Cross Origin Resource Sharing Filter */
        DelegatingFilterProxy corsFilter = new DelegatingFilterProxy("corsFilter", dispatcherContext);
        servletContext.addFilter("corsFilter", corsFilter)
            .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, BaseCtrl.BASE_API_PATH + "/*");

        /* XFrameFilter to prevent clickjacking */
        DelegatingFilterProxy xFrameFilter = new DelegatingFilterProxy("xFrameFilter", dispatcherContext);
        servletContext.addFilter("xFrameFilter", xFrameFilter)
                .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, "/*");

        /* Api Request Logging */
        DelegatingFilterProxy apiLogFilter = new DelegatingFilterProxy("apiLogFilter", dispatcherContext);
        servletContext.addFilter("apiLogFilter", apiLogFilter)
                .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, BaseCtrl.BASE_API_PATH + "/*");

        /* Api Key Authentication */
        DelegatingFilterProxy apiAuthFilter = new DelegatingFilterProxy("apiAuthFilter", dispatcherContext);
        servletContext.addFilter("apiAuthFilter", apiAuthFilter)
                .addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, INCLUDE), false, BaseCtrl.BASE_API_PATH + "/*");

        /**
         * Configure Shiro to track sessions in cookies instead of urls.
         * This fixes an issue where trying to navigate to an admin api endpoint when not authenticated would
         * redirect to something like `/admin/login;jsessionid=EFEF8F5FFF5033FA4C309222266185A1`
         * instead of `/admin/login`
         */
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    }
}
