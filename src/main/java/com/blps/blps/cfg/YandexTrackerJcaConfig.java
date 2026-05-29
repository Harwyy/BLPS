package com.blps.blps.cfg;

import com.yandex.tracker.jca.api.YandexTrackerConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class YandexTrackerJcaConfig {

    @Bean
    public JndiObjectFactoryBean yandexTrackerConnectionFactory() {
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        String jndiName = "java:jboss/YandexTrackerCF";
        factory.setJndiName(jndiName);
        factory.setExpectedType(YandexTrackerConnectionFactory.class);
        factory.setProxyInterface(YandexTrackerConnectionFactory.class);
        return factory;
    }

    @Bean
    public YandexTrackerConnectionFactory yandexTrackerConnectionFactoryProxy() {
        return (YandexTrackerConnectionFactory) yandexTrackerConnectionFactory().getObject();
    }
}
