package com.kschool.alertplatform.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alert {

    private String id;

    private String userId;

    private String source;

    private String measure;

    private String threshold;

    private String value;

    private String message;

    private String timestamp;
}
