package com.kschool.alertstreaming.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrafficRaw {
    public String totalVehiculosTunel;
    public String totalVehiculosCalle30;
    public String velocidadMediaTunel;
    public String velicidadMediaSuperfice;

}
