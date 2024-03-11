package oi.pp.boot.starter_sample;

import lombok.extern.slf4j.Slf4j;
import oi.pp.boot.starter.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author supanpan
 * @date 2024/03/11
 */
@Slf4j
@SpringBootApplication
public class Application {
    @Value("true")
    private boolean debug;


    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner commandLineRunner(TestService testService) {
        return (args) -> {
            log.info("debug mode: {}", debug);
            log.info(testService.getServiceName());
        };
    }
}
