package oi.pp.boot.properties.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
/**
 * @author panpan
 */
@Slf4j
public class Configuration2 {
    
    @Bean
    public CommandLineRunner clr2() {
        return (args) -> {
            log.info("Configuration2 commandLineRunner");
        };
    }
}
