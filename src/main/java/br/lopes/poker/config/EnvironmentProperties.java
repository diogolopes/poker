package br.lopes.poker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:environment.properties")
@ConfigurationProperties
public class EnvironmentProperties {

    private String datasourcePath;

    public String getDatasourcePath() {
        return datasourcePath;
    }

    public void setDatasourcePath(String datasourcePath) {
        this.datasourcePath = datasourcePath;
    }

}
