package com.kschool.alertplatform.common.model.trafficdensity;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrafficDensityEnriched extends EnrichedEvents {
    public TrafficDensityEnriched(String id, String source, String measure, Double value, String timestamp) {
        super(id, source, measure, value, timestamp);
    }
}
