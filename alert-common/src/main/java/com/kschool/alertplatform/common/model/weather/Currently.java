package com.kschool.alertplatform.common.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Currently {
    private double apparentTemperature;
    private double cloudCover;
    private double dewPoint;
    private double humidity;
    private String icon;
    private double ozone;
    private double precipIntensity;
    private double precipProbability;
    private double pressure;
    private String summary;
    private double temperature;
    private double time;
    private double uvIndex;
    private double visibility;
    private double windBearing;
    private double windGust;
    private double windSpeed;
}
