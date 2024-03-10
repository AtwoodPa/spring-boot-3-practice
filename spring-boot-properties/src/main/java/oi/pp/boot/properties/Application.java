package oi.pp.boot.properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oi.pp.boot.properties.props.DbProperties;
import oi.pp.boot.properties.props.JavastackProperties;
import oi.pp.boot.properties.props.MemberProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

/**
 * @author supanpan
 * @date 2024/03/10
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
// 在启动类上添加@EnableConfigurationProperties注解，指定需要注入的配置类
//@EnableConfigurationProperties(value = {JavastackProperties.class, MemberProperties.class})

@ConfigurationPropertiesScan

public class Application {
    private final DbProperties dbProperties;
    private final JavastackProperties javastackProperties;
    private final MemberProperties memberProperties;

    @Value("${server.port}")
    private int serverPort;
    // 通过使用命令行参数，可以在启动应用程序时传递参数
    // spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }


    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            log.info("server.port: {}", serverPort);
            log.info("db properties: {}", dbProperties);
            log.info("javastack properties: {}", javastackProperties);
            log.info("member properties: {}", memberProperties);

        };
    }
}
