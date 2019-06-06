# MADLERT (Kschool Big Data Architecture TFM 8 Edition)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

**MADLERT** es una plataforma analítica en tiempo real que informa del estado de diferentes agentes relacionados con la *contaminación en la ciudad de **Madrid** (calidad del aire, tiempo, densidad de tráfico). Las principales funciones de la plataforma son:

  - Servicio de configuración de alertas para cada medición deseada.
  - Servicio de notificaciones de alertas en tiempo real al usuario por email cuando una medida supera el límite establecido en la configuración de la alerta.
  - Generación de dashboards en tiempo real del estado de las mediciones, así como la posibilidad de crear dashboards personalizados.

# Arquitectura

![alt text](https://github.com/AdrianSanzRodrigo/alert-platform-notifier/blob/master/resources/madlert_architecture_overview.png)

### Data Sources

Las fuentes de datos provienen del [portal de datos abiertos del ayuntamiento de Madrid](https://datos.madrid.es/portal/site/egob/):
  - [Calidad del aire](https://datos.madrid.es/portal/site/egob/menuitem.c05c1f754a33a9fbe4b2e4b284f1a5a0/?vgnextoid=41e01e007c9db410VgnVCM2000000c205a0aRCRD&vgnextchannel=374512b9ace9f310VgnVCM100000171f5a0aRCRD&vgnextfmt=default) en formato csv.
  - [Densidad de tráfico](https://datos.madrid.es/portal/site/egob/menuitem.c05c1f754a33a9fbe4b2e4b284f1a5a0/?vgnextoid=d5ec05dc4d1ab410VgnVCM2000000c205a0aRCRD&vgnextchannel=374512b9ace9f310VgnVCM100000171f5a0aRCRD&vgnextfmt=default) en formato xml.
  - [Tiempo](https://api.darksky.net/forecast/6ece3b408a49bf97ef08309d05355930/40.4167,-3.7037?exclude=minutely,hourly,daily,flags,alerts) en formato json.

### Data Ingestion Layer

[Nifi](https://nifi.apache.org/) es el encargado de realizar las peticiones */GET* a los endpoints de cada fuente, donde a través de un CRON las ejecuta cada minuto. A su vez, procesa los distintos process groups y envía los datos en formato json a sus correspondientes topics de Kafka: *raw-weather, raw-air-quality, raw-traffic-density*.
![alt text](https://github.com/AdrianSanzRodrigo/alert-platform-notifier/blob/master/resources/nifi_process_groups.png)

### Data Processing Layer

En esta capa diversos procesos de [Kafa Streams](https://kafka.apache.org/documentation/streams/) concurren para enriquecer en tiempo real los datos provenientes de las fuentes en raw:
  - **WeatherEnrichmentApp**: Enriquece los datos del tiempo recibidos como *WeatherRaw* y se generan en el topic *enriched-weather* como *WeatherEnriched*.
  - **AirQualityEnrichmentApp**: Enriquece los datos de la calidad del aire recibidos como *AirQualityRaw* y se generan en el topic *enriched-air-quality* como *AirQualityEnriched*.
  - **TrafficDensityEnrichmentApp**: Enriquece los datos de la densidad de tráfico recibidos como *TrafficDensityRaw* y se generan en el topic *enriched-traffic-density* como *TrafficDensityEnriched*.
  - **AlertConfigAggregatorApp**: Recibe en el topic *alerts-config* la acción del usuario sobre su configuración de alertas donde se realiza una agregación de los datos sobre una *KTable* donde se generan las modificaciones del usuario agregadas en el topic *aggregated-alerts-config*.
  - **AlertGeneratorApp**: Une en un mismo *Kstream* los datos recibidos por los procesos de *WeatherEnrichmentApp*, *AirQualityEnrichmentApp* y *AlertGeneratorApp* y cuando se reciben nuevas mediciones efectúa un join por tipo de measure asociada a n usuarios con la KTable generada por el proceso de *AlertConfigAggregatorApp*. Por último, filtra aquellas medidas que cumplen con la configuración del usuario (que estén por encima/debajo de un valor límite) y se genera la alerta en el topic *alerts* con la estructura de cuerpo de mensaje de email que el usuario recibirá.

### Data Serving Layer

Esta capa se encarga tanto de aprovisionar los datos para que el usuario interactúe con ellos. Se encuentran tres componentes principales:

  - [**Druid**](http://druid.io/): Sirve como capa de conexión entre los datos enriquecidos recibidos en los topics de Kafka y la capa de visualización formada por Superset. Indexa los datos en tiempo real y permite que Superset pueda actualizar los dashboards en tiempo real.
  - [**SpringBoot**](https://spring.io/projects/spring-boot): **AlertConfigServiceApp** es un servicio encargado tanto de notificar al usuario por email de las alertas recibidas (**AlertNotifierService**) como de enviar las modificaciones de la configuración de alertas del usuario (**AlertConfigurationService**)
  - [**Couchbase**](https://www.couchbase.com/): Se encarga de guardar en documentos json la configuración de alertas del usuario, teniendo como key el DNI del mismo. A su vez, el usuario puede consultar en cualquier momento la configuración que tiene definida. El json guardado sigue la siguiente estructura:
  ```json
{
  "alertConfigs": [
    {
      "action": "CREATE",
      "id": "weather_35194f22-ffb3-4533-92c8-02b842fc0756",
      "limitType": "upper",
      "measure": "temperature",
      "source": "weather",
      "threshold": 35,
      "timestamp": "2019-06-05 01:02:30.169",
      "userId": "03933333L"
    }
  ]
}
```
### Visualization Layer

Por último, esta capa se encarga de representar en tiempo real información valiosa de los eventos que se van recibiendo, así de poder consultar agregaciones sobre los datos o custom queries. Esto es posible gracias a [Superset](https://superset.incubator.apache.org/), generando dashboards y proporcionando a simple vista la monitorización de los diferentes agentes de la contaminación en Madrid:
![alt text](https://github.com/AdrianSanzRodrigo/alert-platform-notifier/blob/master/resources/air_quality_graph.png)

# Modelado de datos

#### AirQualityRaw

| Field | Type |
| ------ | ------ |
| PROVINCIA | String |
| MUNICIPIO | String |
| ESTACION | String |
| MAGNITUD | String |
| PUNTO_MUESTREO | String |
| ANO | String |
| MES | String |
| DIA | String |
| Hx | String |
| Vx | Double |
Siendo Hx y Vx cada x correspondiente a una hora del día. El mapeo de los campos *MAGNITUD*  y *PUNTO_MUESTREO* se han realizado conforme al siguiente [documento](https://datos.madrid.es/FWProjects/egob/Catalogo/MedioAmbiente/Aire/Ficheros/Interprete_ficheros_%20calidad_%20del_%20aire_global.pdf).

#### AirQualityEnriched

| Field | Type |
| ------ | ------ |
| id | String |
| source | String |
| measure | String |
| value | Double |
| station | String |
| timestamp | String |

#### TrafficDensityRaw

| Field | Type |
| ------ | ------ |
| DatosTrafico | DatosTrafico |

#### DatosTrafico

| Field | Type |
| ------ | ------ |
| DatoGlobal | List<DatoGlobal> |

#### DatosGlobal

| Field | Type |
| ------ | ------ |
| Nombre | String |
| Nombre | VALOR |
| Nombre | FECHA |

#### TrafficDensityEnriched

| Field | Type |
| ------ | ------ |
| id | String |
| source | String |
| measure | String |
| value | Double |
| timestamp | String |

#### WeatherRaw

| Field | Type |
| ------ | ------ |
| currently | Currently |

#### Currently

| Field | Type |
| ------ | ------ |
| apparentTemperature | Double |
| cloudCover | Double |
| dewPoint | Double |
| humidity | Double |
| icon | String |
| ozone | Double |
| precipIntensity | Double |
| precipProbability | Double |
| pressure | Double |
| summary | String |
| temperature | Double |
| time | Double |
| uvIndex | Double |
| visibility | Double |
| windBearing | Double |
| windGust | Double |
| windSpeed | Double |

#### WeatherEnriched

| Field | Type |
| ------ | ------ |
| id | String |
| source | String |
| measure | String |
| skyState | String |
| value | Double |
| timestamp | String |

# Instalación