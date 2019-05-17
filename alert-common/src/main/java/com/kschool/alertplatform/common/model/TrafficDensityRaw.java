package com.kschool.alertplatform.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrafficDensityRaw {
    public String totalVehiculosTunel;
    public String totalVehiculosCalle30;
    public String velocidadMediaTunel;
    public String velicidadMediaSuperfice;

}
