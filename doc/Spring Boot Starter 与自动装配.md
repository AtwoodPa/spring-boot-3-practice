# Spring Boot Starter 与自动装配

## 1、Starter

Starter可以理解为Spring Boot中的一站式集成启动器，它包含了一系列可以集成到应用中的依赖项（dependencies），可以快速一站式集成Spring组件及其他框架，而不需要到处找示例代码、依赖包及复杂的组件配置，我们只需要通过简单的参数配置就可以实现“**开箱即用**”

一个完整的Spring Boot Starter一般需要包含以下组件：

- 完成自动配置的自动配置模块

- 为自动配置模块提供的所有依赖项

通过添加一个Starter可以轻松的将所需的功能快速的集成到应用中。比如，我们想使用Mybatis这款ORM框架访问数据库，只要加入官网提供的mybatis-spring-boot-starter启动器依赖即可：

```xml
<!-- MyBatis Spring Boot Starter -->  
    <dependency>  
        <groupId>org.mybatis.spring.boot</groupId>  
        <artifactId>mybatis-spring-boot-starter</artifactId>  
        <version>x.x.x</version>  
    </dependency> 
```

加入依赖后就能完成默认的自动配置并能直接使用。



## 2、自动配置

### 2.1、概述

Spring Boot Starter的核心原理就是自动配置，这也是整个Spring Boot框架的核心，开发者只需要按约定提供些许配置参数就能完成各种技术复杂组件的自动组装配置，这正是Spring Boot框架能迅速上手使用的原因。

Spring Boot官方所有的自动配置类都是由spring-boot-autoconfigure这个模块提供的，只要引入了对应组件的依赖，Spring Boot扫描到相关类时才会自动配置，没有引入是不会启用自动配置的。

### 2.2、命名规范

Spring Boot自动配置类一般以**XxxAutoConfiguration**命名，比如以下几个组件：

- RabbitAutoConfiguration

- FlywayAutoConfiguration

- RedisAutoConfiguration

自动配置类需要注册到Spring Boot指定的自动配置文件中，**低版本为类路径下的META-INF/spring.factories自动配置文件**，Spring Boot 2.7对自动配置类的注册文件路径进行了变更，**新的自动配置类注册文件路径如下**：

<mark>META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports</mark>

![](http://imgcom.static.suishenyun.net/202403111026708.png)

### 2.3、自动配置文件的加载原理

Spring Boot开启自动配置使用的是@EnableAutoConfiguration注解，一般使用组合了它的@SpringBootApplication主注解即可，@EnableAutoConfiguration注解的源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	/**
	 * Environment property that can be used to override when auto-configuration is
	 * enabled.
	 */
	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	String[] excludeName() default {};

}

```

- **@AutoConfigurationPackage**
  
  - 注册需要自动配置的包，如果不指定就是当前注解所在类的包

- **@Import**
  
  - 导入配置类，这里导入的是AutoConfigurationImportSelector（实现类ImportSelector接口类）

如上所述，自动配置@Import注解导入的是AutoConfigurationImportSelector.class类，这也是这个注解的关键所在，它实现了ImportSelector接口：

```java
public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 * @return the class names, or an empty array if none
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

	/**
	 * Return a predicate for excluding classes from the import candidates, to be
	 * transitively applied to all classes found through this selector's imports.
	 * <p>If this predicate returns {@code true} for a given fully-qualified
	 * class name, said class will not be considered as an imported configuration
	 * class, bypassing class file loading as well as metadata introspection.
	 * @return the filter predicate for fully-qualified candidate class names
	 * of transitively imported configuration classes, or {@code null} if none
	 * @since 5.2.4
	 */
	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}

}
```

这个注解有两个方法

- **selectImports**（抽象方法）
  
  - 选择要导入配置类

- **getExclusionFilter**（接口默认方法）
  
  - 用于返回要排除的类

AutoConfigurationImportSeletor类的selectImports方法的源码:

```java
@Override
public String[] selectImports(AnnotationMetadata annotationMetadata) {
	if (!isEnabled(annotationMetadata)) {
		return NO_IMPORTS;
	}
	AutoConfigurationEntry autoConfigurationEntry
                 = getAutoConfigurationEntry(annotationMetadata);
	return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
}
```

下面是**isEnabled**方法

这个函数的主要作用是判断当前是否启用了自动配置，如果当前类是AutoConfigurationImportSelector类，则根据属性值决定是否启用自动配置；否则，直接启用自动配置。

```java
protected boolean isEnabled(AnnotationMetadata metadata) {
		if (getClass() == AutoConfigurationImportSelector.class) {
			return getEnvironment().getProperty(EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY, Boolean.class, true);
		}
		return true;
	}
```

- 首先，它检查当前类是否等于AutoConfigurationImportSelector类。如果是，则通过getEnvironment().getProperty()方法获取一个属性值，该属性值用于覆盖自动配置的启用状态。如果该属性值为true，则返回true，表示自动配置已启用；如果该属性值为false或其他非true值，则返回false，表示自动配置未启用。

- 如果当前类不等于AutoConfigurationImportSelector类，则直接返回true，表示自动配置已启用。

再根据**getAutoConfigurationEntry**方法进入**getCandidateConfigurations**方法

```java
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    // 导入候选自动配置列表	
   List<String> configurations = ImportCandidates.load(AutoConfiguration.class, getBeanClassLoader())
			.getCandidates();
		Assert.notEmpty(configurations,
				"No auto configuration classes found in "
						+ "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports. If you "
						+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
```

**getCandidateConfigurations**用于获取自动配置的类名列表。

- 它通过加载AutoConfiguration类的候选者，并返回这些候选者的类名列表。

- 如果候选者列表为空，则会抛出一个异常。

- 这个方法主要用于Spring Boot的自动配置功能中，帮助Spring Boot在启动时自动加载相应的配置类，以实现快速开发。

### 2.4、自动配置原理

自动配置文件被加载后，就会注册里面提供的自动配置类了

例如：org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration

这是一个缓存的自动配置类

```java
@AutoConfiguration(after = { CouchbaseDataAutoConfiguration.class, HazelcastAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class, RedisAutoConfiguration.class })
@ConditionalOnClass(CacheManager.class)
@ConditionalOnBean(CacheAspectSupport.class)
@ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")
@EnableConfigurationProperties(CacheProperties.class)
@Import({ CacheConfigurationImportSelector.class, CacheManagerEntityManagerFactoryDependsOnPostProcessor.class })
public class CacheAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
		return new CacheManagerCustomizers(customizers.orderedStream().toList());
	}

	@Bean
	public CacheManagerValidator cacheAutoConfigurationValidator(CacheProperties cacheProperties,
			ObjectProvider<CacheManager> cacheManager) {
		return new CacheManagerValidator(cacheProperties, cacheManager);
	}

	@ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
	@ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
	static class CacheManagerEntityManagerFactoryDependsOnPostProcessor
			extends EntityManagerFactoryDependsOnPostProcessor {

		CacheManagerEntityManagerFactoryDependsOnPostProcessor() {
			super("cacheManager");
		}

	}

	/**
	 * Bean used to validate that a CacheManager exists and provide a more meaningful
	 * exception.
	 */
	static class CacheManagerValidator implements InitializingBean {

		....

	}

	/**
	 * {@link ImportSelector} to add {@link CacheType} configuration classes.
	 */
	static class CacheConfigurationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			....
		}

	}

}
```

#### @AutoConfiguration

@AutoConfiguration注解指定了该类是一个自动配置类，并指定了它依赖的其他自动配置类

其源码如下:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore
@AutoConfigureAfter
public @interface AutoConfiguration {

	@AliasFor(annotation = Configuration.class)
	String value() default "";

	@AliasFor(annotation = AutoConfigureBefore.class, attribute = "value")
	Class<?>[] before() default {};

	@AliasFor(annotation = AutoConfigureBefore.class, attribute = "name")
	String[] beforeName() default {};

	@AliasFor(annotation = AutoConfigureAfter.class, attribute = "value")
	Class<?>[] after() default {};

	@AliasFor(annotation = AutoConfigureAfter.class, attribute = "name")
	String[] afterName() default {};

}

```



自动配置注解@AutoConfiguration组合了以下三个注解：

- @Configuration(proxyBeanMethods = false)
  
  - 配置类注解，并不代理@Bean方法

- @AutoConfigureBefore
  
  - 自动配置在XX配置之前

- @AutoConfigureAfter
  
  - 自动配置在XX配置之后

#### @ConditionalOn*

在自动配置类中还有各种@ConditionalOn*的注解，这是一种条件注解，表示在满足指定条件时才会自动配置。

例如，在类**CacheAutoConfiguration**中

- @**ConditionalOnClass**(CacheManager.class)注解表示只有在类路径下存在CacheManager类时，该自动配置类才会生效

- @**ConditionalOnBean**(CacheAspectSupport.class)注解表示只有当存在CacheAspectSupportBean时，该自动配置类才会生效

- @**ConditionalOnMissingBean**(value = CacheManager.class, name = "cacheResolver")注解表示只有在当前应用没有自定义的CacheManager Bean且没有名为cacheResolver的CacheManager Bean时，该自动配置类才会创建一个CacheManager Bean

### 2.5、邮件Starter

本节介绍spring-boot-starter-mail邮件启动器的集成与应用。

Spring 框架提供了一个发送邮件的抽象和实现：

- import org.springframework.mail.javamail.JavaMailSender

- import org.springframework.mail.javamail.JavaMailSenderImpl

MailSenderAutoConfiguration同样被注册在新的AutoConfiguration.imports自动配置文件中，该配置源码如下：

```java
@AutoConfiguration
@ConditionalOnClass({ MimeMessage.class, MimeType.class, MailSender.class })
@ConditionalOnMissingBean(MailSender.class)
@Conditional(MailSenderCondition.class)
@EnableConfigurationProperties(MailProperties.class)
@Import({ MailSenderJndiConfiguration.class, MailSenderPropertiesConfiguration.class })
public class MailSenderAutoConfiguration {
	static class MailSenderCondition extends AnyNestedCondition {
		MailSenderCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}
		@ConditionalOnProperty(prefix = "spring.mail", name = "host")
		static class HostProperty {
		}
		@ConditionalOnProperty(prefix = "spring.mail", name = "jndi-name")
		static class JndiNameProperty {
		}
	}
}
```

#### 发邮件实践

首先创建一个spring-boot-mail工程，在Maven的pom.xml配置文件中加入

spring-boot-starter-mail邮件启动器依赖

```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
</dependencies>
```

然后在应用的application.properties配置文件中加入邮件自动配置参数：

```xml
spring:
  mail:
    host: smtp.exmail.qq.com
#    替换成自己的邮箱
    username: xxx@xxx.com
#    替换成自己的邮箱密码
    password: xxx
    properties:
      "[mail.smtp.socketFactory.class]": javax.net.ssl.SSLSocketFactory
      "[mail.smtp.socketFactory.fallback]": false
      "[mail.smtp.socketFactory.port]": 465
      "[mail.smtp.connectiontimeout]": 5000
      "[mail.smtp.timeout]": 3000
      "[mail.smtp.writetimeout]": 5000

mail:
#  替换成自己的邮箱
  from: xxx@xxx.com
  personal: PP
#  替换成自己的邮箱
  bcc: xxx@xxx.com
  subject: Spring Boot 发邮件测试主题
```

创建EmailController

```java
/**
 * @author supanpan
 * @date 2024/03/11
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class EmailController {
    private final JavaMailSender javaMailSender;

    private final MailProperties mailProperties;


    @RequestMapping("/sendEmail")
    @ResponseBody
    public boolean sendEmail(@RequestParam("email") String email,
                             @RequestParam("text") String text) {
        try {
            MimeMessage msg = createMimeMsg(email, text, "java.png");
            javaMailSender.send(msg);
        } catch (Exception ex) {
            log.error("邮件发送失败：", ex);
            return false;
        }
        return true;
    }


    /**
     * 创建复杂邮件
     * @param email
     * @param text
     * @param attachmentClassPath
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private MimeMessage createMimeMsg(String email, String text, String attachmentClassPath) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(msg, true);
        mimeMessageHelper.setFrom(mailProperties.getFrom(), mailProperties.getPersonal());
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setBcc(mailProperties.getBcc());
        mimeMessageHelper.setSubject(mailProperties.getSubject());
        mimeMessageHelper.setText(text);
        // 添加附件，java.png是在resources目录下的文件
        mimeMessageHelper.addAttachment("附件",
                new ClassPathResource(attachmentClassPath));
        return msg;
    }

    /**
     * 创建简单邮件
     * @param email
     * @param text
     * @return
     */
    private SimpleMailMessage createSimpleMsg(String email, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailProperties.getFrom());
        msg.setTo(email);
        msg.setBcc(mailProperties.getBcc());
        msg.setSubject(mailProperties.getSubject());
        msg.setText(text);
        return msg;
    }
}

```

### 2.6、自定义Starter

本节实现一个简单的自定义Spring Boot Starter

#### 2.6.1、创建Starter工程

根据Starter定义的规范，第三方的应用应该以*-spring-boot-starter的形式命名，所以接下来创建一个**pp-spring-boot-starter**的工程

#### 2.6.2、创建自动配置类

创建一个简单的自动配置类：

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "pp.starter", name = "enabled", havingValue = "true")
public class TestServiceAutoConfiguration {

    @Bean
    public TestService testService() {
        return new TestService();
    }

}
```

该配置类就是判断Spring环境配置中是否有pp.starter.enabled=true这个参数的值，如果有就配置一个TestService的Bean

```java
public class TestService {

        public String getServiceName() {
            return "PP-Starter";
        }
}
```

#### 2.6.3、注册自动配置类（spring.factories）

在Spring Boot 3.0应用的resource资源目录下创建imports自动配置文件：

META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

添加要注册的自动配置类：

```xml
oi.pp.boot.starter.config.TestServiceAutoConfiguration
```

项目工程目录如下：

![](http://imgcom.static.suishenyun.net/202403111215656.png)

#### 2.6.4、使用Starter

经过上述步骤，一个自定义的Starter就搭建好了，如果想使用这个自定义Starter，首先在本地打包，再新建另外一个应用并引用它进行测试。

创建一个pp-spring-boot-starter-sample，并添加自定义starter依赖：

```xml
    <dependencies>
        <dependency>
            <groupId>oi.pp.boot.starter</groupId>
            <artifactId>pp-spring-boot-starter</artifactId>
            <version>1.0</version>
            <scope>simple</scope>
        </dependency>
    </dependencies>
```

application.yml:

```xml
pp:
  starter:
   enabled: true

debug: true

# 排除指定的自动配置项
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

创建启动器Application：

```java

```
