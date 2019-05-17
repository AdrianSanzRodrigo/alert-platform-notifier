package com.kschool.alertplatform.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class EnrichedEvents {

    public String id;

    public String source;

    public String measure;

    public Double value;

    public String timestamp;

    public EnrichedEvents(String id, String source, String measure, Double value, String timestamp) {
        this.id=id;
        this.source=source;
        this.measure=measure;
        this.value=value;
        this.timestamp=timestamp;
    }

}
