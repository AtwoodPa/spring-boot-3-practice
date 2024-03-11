package oi.pp.boot.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author supanpan
 * @date 2024/03/11
 */
@EnableConfigurationProperties({MailProperties.class})
@RequiredArgsConstructor
@SpringBootApplication
@RestController
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


}
