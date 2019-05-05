package com.kschool.alertplatform.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertConfig {

    private String id;

    private String source;

    private String measure;

    private String threshold;

    private String limitType;

    private String timestamp;

    private String action;
}
