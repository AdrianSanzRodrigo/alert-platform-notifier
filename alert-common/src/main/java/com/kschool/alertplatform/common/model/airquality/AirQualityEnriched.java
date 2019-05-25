package com.kschool.alertplatform.common.model.airquality;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirQualityEnriched extends EnrichedEvents {
    public String source;
    public String station;
    public String magnitude;
    public String sampling_point;
    public String date;
}
