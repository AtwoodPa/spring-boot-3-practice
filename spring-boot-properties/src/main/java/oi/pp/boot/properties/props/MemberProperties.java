package oi.pp.boot.properties.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author panpan
 * 构造器绑定
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "member")
public class MemberProperties {

    private String name;
    private int sex;
    private int age;
    private String country;
    private Date birthday;

    public MemberProperties(String name, int sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    @ConstructorBinding// 通过构造器绑定属性
    public MemberProperties(String name,
                            int sex,
                            int age,
                            // 为参数设置默认值
                            @DefaultValue("China") String country,
                            // 为参数设置日期格式
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date birthday) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.country = country;
        this.birthday = birthday;
    }

}