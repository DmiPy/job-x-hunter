package dev.dmitriy.job_x_hunter.jobparsing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "adzuna")
public class AdzunaProperties {
    private String appId;
    private String appKey;
    private String countryCode;
}