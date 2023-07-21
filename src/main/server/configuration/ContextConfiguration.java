package server.configuration;

import server.aspect.LoggingAnnotatedMethodsAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ContextConfiguration {

    @Value("${profile.package}")
    private String profilePackage;


    @Bean
    public LoggingAnnotatedMethodsAspect aspectAnnotated() {
        return new LoggingAnnotatedMethodsAspect(profilePackage);
    }
}
