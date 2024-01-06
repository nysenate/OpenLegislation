package gov.nysenate.openleg.config;

import gov.nysenate.openleg.common.util.AsciiArt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebMvc
@EnableAsync
@EnableScheduling
@ComponentScan("gov.nysenate.openleg")
@Import({DatabaseConfig.class, SecurityConfig.class, ApplicationConfig.class, WebSocketsConfig.class})
public class WebApplicationConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);
    private static final String resourcePath = "/static/**";
    private static final String resourceLocation = "/static/";
    private static final int CACHE_PERIOD = 64000;

    private final ApplicationConfig appConfig;

    @Autowired
    public WebApplicationConfig(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        logger.info("{}", AsciiArt.OPENLEG_LOGO.getText().replace("DATE", LocalDateTime.now().toString()));
    }

    /** Sets paths that should not be intercepted by a controller (e.g css/ js/). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("Registering resource path {} for files under {}", resourcePath, resourceLocation);
        registry.addResourceHandler(resourcePath).addResourceLocations(resourceLocation).setCachePeriod(CACHE_PERIOD);
        logger.info("Registering resource path {} for files under {}", "/favicon.ico", resourceLocation);
        registry.addResourceHandler("/favicon.ico").addResourceLocations(resourceLocation).setCachePeriod(CACHE_PERIOD);
        logger.info("Registering resource path {} for files under {}", "/apple-touch-icon.png", resourceLocation);
        registry.addResourceHandler("/apple-touch-icon.png").addResourceLocations(resourceLocation).setCachePeriod(CACHE_PERIOD);
    }

    /**
     * This view resolver will map view names returned from the controllers to html files stored in the
     * configured 'prefix' url.
     */
    @Bean(name = "viewResolver")
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/static/dist/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        converters.add(jackson2Converter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(appConfig.objectMapper());
        return converter;
    }
}
