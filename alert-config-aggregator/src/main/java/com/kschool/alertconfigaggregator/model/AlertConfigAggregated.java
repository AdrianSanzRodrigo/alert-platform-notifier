package com.kschool.alertconfigaggregator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertConfigAggregated {

    private String id;

    private String userId;

    private String source;

    private String measure;

    private String threshold;

    private String limitType;

    private String timestamp;
}
