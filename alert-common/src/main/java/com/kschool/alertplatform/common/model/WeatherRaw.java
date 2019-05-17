package com.kschool.alertplatform.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherRaw {
    public Double rainAmount;
}
