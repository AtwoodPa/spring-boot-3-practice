package oi.pp.boot.properties.config;

import oi.pp.boot.properties.props.OtherMember;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * @author supanpan
 * @date 2024/03/10
 * 通过@Import注解引入Configuration1和Configuration2的好处是，可以将多个配置类组合在一起，这样可以更好的管理配置类。
 * 不同的配置类可以专注于不同的功能，例如：
 * MainConfiguration：项目主要配置
 * SecurityConfiguration：安全配置
 * DataSourceConfiguration：数据源配置
 * RedisConfiguration：Redis配置
 * MongoDBConfiguration：MongoDB配置
 */
@Profile("main")
@SpringBootConfiguration
@Import({Configuration1.class, Configuration2.class})
public class MainConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConfigurationProperties(prefix = "member")
    public OtherMember otherMember() {
        return new OtherMember();
    }
}
