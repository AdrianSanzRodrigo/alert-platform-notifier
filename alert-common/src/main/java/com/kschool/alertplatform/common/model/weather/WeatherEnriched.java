package com.kschool.alertplatform.common.model.weather;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeatherEnriched extends EnrichedEvents {
    public String skyState;

    public WeatherEnriched(String id, String source, String measure, Double value, String skyState, String timestamp) {
        super(id, source, measure, value, timestamp);
        this.skyState = skyState;
    }
}
