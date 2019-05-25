package com.kschool.alertplatform.common.model.trafficdensity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrafficDensityRaw {
    public List<DatoGlobal> datoGlobal;

}
