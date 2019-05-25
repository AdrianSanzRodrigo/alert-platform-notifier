package com.kschool.alertplatform.common.model.trafficdensity;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrafficDensityEnriched extends EnrichedEvents {
    public String source;
    public String totalVehicles;
    public String totalVehiclesStreet30;
    public String avgSpeedTunnel;
    public String avgSpeedRoad;
}
