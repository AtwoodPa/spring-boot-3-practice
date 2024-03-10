package oi.pp.boot.properties.props;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author supanpan
 * @date 2024/03/10
 */
@Data
@Component
@PropertySource(value = {"config/db-config.properties"})
public class DbProperties {
    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;
}
