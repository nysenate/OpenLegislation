package gov.nysenate.openleg.service.mail;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SendMailServiceTestConf {
    @Bean
    @Primary
    public SendMailService sendMailService() {
        return Mockito.mock(SendMailService.class);
    }
}