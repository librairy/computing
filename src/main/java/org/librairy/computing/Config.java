package org.librairy.computing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Configuration("computing")
@ComponentScan({"org.librairy"})
@PropertySource({"classpath:computing.properties","classpath:boot.properties"})
public class Config {

    //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
