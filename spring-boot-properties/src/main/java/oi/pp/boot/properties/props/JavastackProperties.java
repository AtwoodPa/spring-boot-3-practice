package oi.pp.boot.properties.props;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Data
@Validated
// 若想使得@ConfigurationProperties注解生效，需要在启动类上添加@EnableConfigurationProperties注解
@ConfigurationProperties(prefix = "javastack")
public class JavastackProperties {

    private boolean enabled;

    @NotNull
    private String name;

    private String site;

    private String author;

    private List<String> users;

    private Map<String, String> params;

    private Security security;

}

@Data
class Security {

    private String securityKey;

    private String securityCode;

}