package oi.pp.boot.properties.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/**
 * @author panpan
 */
@Slf4j
public class Configuration1 {
    
    @Bean
    public CommandLineRunner clr1() {
        return (args) -> {
            log.info("Configuration1 commandLineRunner");
        };
    }
}
