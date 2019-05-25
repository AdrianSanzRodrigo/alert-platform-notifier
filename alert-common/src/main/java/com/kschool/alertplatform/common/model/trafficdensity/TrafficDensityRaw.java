package com.kschool.alertplatform.common.model.trafficdensity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficDensityRaw {
    @JsonProperty("DatoGlobal")
    public List<DatoGlobal> DatoGlobal;

}
