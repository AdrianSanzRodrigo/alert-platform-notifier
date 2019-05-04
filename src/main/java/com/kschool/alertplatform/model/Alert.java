package com.kschool.alertplatform.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Alert {
    private String id;
    private String threshold;
    private String time;
    private String source;
    private String measure;
    private String currentValue;
    private String cause;
}
