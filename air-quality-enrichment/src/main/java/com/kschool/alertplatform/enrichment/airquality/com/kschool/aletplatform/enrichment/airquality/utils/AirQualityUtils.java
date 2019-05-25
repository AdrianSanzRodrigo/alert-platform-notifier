package com.kschool.alertplatform.enrichment.airquality.com.kschool.aletplatform.enrichment.airquality.utils;

import com.kschool.alertplatform.common.model.airquality.AirQualityRaw;
import java.util.*;

public class AirQualityUtils {

    static Map<String, String> stationDict = new HashMap<>();
    static Map<String, String> magnitudeDict = new HashMap<>();

    public static Map<String, String> getStationDict() {
        stationDict.put("28079001", "Pº Recoletos");
        stationDict.put("28079002", "Glta. de Carlos V");
        stationDict.put("28079003", "Pza. del Carmen");
        stationDict.put("28079035", "Pza. del Carmen");
        stationDict.put("28079004", "Pza. de España");
        stationDict.put("28079005", "Barrio del Pilar");
        stationDict.put("28079039", "Barrio del Pilar");
        stationDict.put("28079006", "Pza. Dr. Marañón");
        stationDict.put("28079007", "Pza. M. de Salamanca");
        stationDict.put("28079008", "Escuelas Aguirre");
        stationDict.put("28079009", "Pza. Luca de Tena");
        stationDict.put("28079010", "Cuatro Caminos");
        stationDict.put("28079038", "Cuatro Caminos");
        stationDict.put("28079011", "Av. Ramón y Cajal");
        stationDict.put("28079012", "Pza. Manuel Becerra");
        stationDict.put("28079013", "Vallecas");
        stationDict.put("28079040", "Vallecas");
        stationDict.put("28079014", "Pza. Fdez. Ladreda");
        stationDict.put("28079015", "Pza. Castilla");
        stationDict.put("28079016", "Arturo Soria");
        stationDict.put("28079017", "Villaverde Alto");
        stationDict.put("28079018", "C/ Farolillo");
        stationDict.put("28079019", "Huerta Castañeda");
        stationDict.put("28079020", "Moratalaz");
        stationDict.put("28079036", "Moratalaz");
        stationDict.put("28079022", "Pº. Pontones");
        stationDict.put("28079023", "Final C/ Alcalá");
        stationDict.put("28079024", "Casa de Campo");
        stationDict.put("28079025", "Santa Eugenia");
        stationDict.put("28079026", "Urb. Embajada (Barajas)");
        stationDict.put("28079027", "Barajas");
        stationDict.put("28079047", "Méndez Álvaro");
        stationDict.put("28079048", "Pº. Castellana");
        stationDict.put("28079049", "Retiro");
        stationDict.put("28079050", "Pza. Castilla");
        stationDict.put("28079054", "Ensanche Vallecas");
        stationDict.put("28079055", "Urb. Embajada (Barajas)");
        stationDict.put("28079056", "Plaza Elíptica");
        stationDict.put("28079057", "Sanchinarro");
        stationDict.put("28079058", "El Pardo");
        stationDict.put("28079059", "Parque Juan Carlos I");
        stationDict.put("28079086", "Tres Olivos");
        stationDict.put("28079060", "Tres Olivos");
        return stationDict;
    }

    public static Map<String, String> getMagnitudeDict() {
        magnitudeDict.put("1", "Dióxido de Azufre");
        magnitudeDict.put("6", "Monóxido de Carbono");
        magnitudeDict.put("7", "Monóxido de Nitrógeno");
        magnitudeDict.put("8", "Dióxido de Nitrógeno");
        magnitudeDict.put("9", "Partículas < 2.5 µm");
        magnitudeDict.put("10", "Partículas < 10 µm");
        magnitudeDict.put("12", "Óxidos de Nitrógeno");
        magnitudeDict.put("20", "Tolueno");
        magnitudeDict.put("30", "Benceno");
        magnitudeDict.put("35", "Etilbenceno");
        magnitudeDict.put("37", "Metaxileno");
        magnitudeDict.put("38", "Paraxileno");
        magnitudeDict.put("39", "Ortoxileno");
        magnitudeDict.put("42", "Hexano");
        magnitudeDict.put("43", "Metano");
        magnitudeDict.put("44", "Hidrocarburos no metánicos");

        return magnitudeDict;
    }

    public static Double getCorrespondingValue(AirQualityRaw event) {

        final Map<String, String> values = new HashMap<>();
        values.put("H01", event.getH01());
        values.put("H02", event.getH02());
        values.put("H03", event.getH03());
        values.put("H04", event.getH04());
        values.put("H05", event.getH05());
        values.put("H06", event.getH06());
        values.put("H07", event.getH07());
        values.put("H08", event.getH08());
        values.put("H09", event.getH09());
        values.put("H10", event.getH10());
        values.put("H11", event.getH11());
        values.put("H12", event.getH12());
        values.put("H13", event.getH13());
        values.put("H14", event.getH14());
        values.put("H15", event.getH15());
        values.put("H16", event.getH16());
        values.put("H17", event.getH17());
        values.put("H18", event.getH18());
        values.put("H19", event.getH19());
        values.put("H20", event.getH20());
        values.put("H21", event.getH21());
        values.put("H22", event.getH22());
        values.put("H23", event.getH23());
        values.put("H24", event.getH24());

        Calendar rightNow = Calendar.getInstance();
        String value = "H" + rightNow.get(Calendar.HOUR_OF_DAY);
        return Double.parseDouble(values.get(value));
    }
}
