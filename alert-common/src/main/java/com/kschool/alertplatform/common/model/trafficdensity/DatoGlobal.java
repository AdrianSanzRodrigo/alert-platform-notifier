
package com.kschool.alertplatform.common.model.trafficdensity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatoGlobal {

    @JsonProperty("Nombre")
    public String Nombre;
    @JsonProperty("VALOR")
    public String VALOR;
    @JsonProperty("FECHA")
    public String FECHA;

}
