package com.kschool.alertplatform.common.model.airquality;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AirQualityEnriched extends EnrichedEvents {
    public String station;
    public AirQualityEnriched(String id, String source, String measure, Double value, String station, String timestamp) {
        super(id, source, measure, value, timestamp);
        this.station = station;
    }
}
