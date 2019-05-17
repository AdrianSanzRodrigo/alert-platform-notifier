package com.kschool.alertplatform.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeatherEnriched extends EnrichedEvents {
    public Double avg;

    public WeatherEnriched(String id, Double avg, String source, String measure, Double value, String timestamp) {
        super(id, source, measure, value, timestamp);
        this.avg=avg;
    }
}
