package com.kschool.alertplatform.common.model.trafficdensity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosTrafico {
    @JsonProperty("DatoGlobal")
    public List<DatoGlobal> DatoGlobal;

}
