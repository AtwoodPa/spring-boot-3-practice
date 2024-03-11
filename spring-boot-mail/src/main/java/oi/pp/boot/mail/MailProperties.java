package oi.pp.boot.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author supanpan
 * @date 2024/03/11
 */
@Data
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    /**
     * 发件人
     */
    private String from;

    /**
     * 发件人昵称
     */
    private String personal;

    /**
     * 抄送人
     */
    private String bcc;

    /**
     * 主题
     */
    private String subject;
}
