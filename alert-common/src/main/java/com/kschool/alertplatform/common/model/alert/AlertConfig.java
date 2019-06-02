package com.kschool.alertplatform.common.model.alert;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertConfig {

    private String id;

    private String userId;

    private String source;

    private String measure;

    private Double threshold;

    private String limitType;

    private String timestamp;

    private String action;
}
