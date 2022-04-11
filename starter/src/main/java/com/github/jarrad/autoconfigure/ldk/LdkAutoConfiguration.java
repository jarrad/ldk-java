package com.github.jarrad.autoconfigure.ldk;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({LdkNodeProperties.class})
@Import({LdkNodeConfiguration.class, MempoolSpaceConfiguration.class})
public class LdkAutoConfiguration {

}
