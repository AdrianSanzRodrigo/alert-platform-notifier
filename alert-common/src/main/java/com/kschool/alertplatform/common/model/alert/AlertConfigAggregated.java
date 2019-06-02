package com.kschool.alertplatform.common.model.alert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kschool.alertplatform.common.model.EnrichedEvents;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertConfigAggregated {

    private String id;

    private String userId;

    private String source;

    private String measure;

    private Double threshold;

    private String limitType;

    private String timestamp;

    public boolean matchAllFilters(EnrichedEvents enrichedEvent) {
        return enrichedEvent.getSource().equals(this.source)
                && enrichedEvent.getMeasure().equals(this.measure)
                && hasValidValue(enrichedEvent.getValue(), this.limitType, this.threshold);
    }

    private static boolean hasValidValue(Double eventValue, String limitType, Double filterThreshold) {
        if (limitType.equals("upper")) {
            return isNullOrFulfillFilter(filterThreshold, () -> Math.abs(eventValue) >= filterThreshold);
        }
        else {
            return isNullOrFulfillFilter(filterThreshold, () -> Math.abs(eventValue) <= filterThreshold);
        }
    }

    private static boolean isNullOrFulfillFilter(Object object, Supplier<Boolean> filter) {
        return object == null || filter.get();
    }
}
