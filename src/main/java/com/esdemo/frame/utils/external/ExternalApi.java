package com.esdemo.frame.utils.external;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external-api")
public class ExternalApi{
    private String accountHost;
    private String flowmoneyHost;
    private String coreHost;
}