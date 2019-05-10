package com.kschool.alertstreaming.utils;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertyUtils {

    private static final AlertLogger logging = new AlertLogger(PropertyUtils.class.getName());

    public static Properties loadConfig(String path) {
        File file = new File(path);
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(file));
        } catch (Exception e) {
            logging.error("Properties file not found!");
            System.exit(-1);
        }
        return prop;
    }

    public static Boolean checkRequiredProperties(List<String> reqProperties, Properties prop) {
        String listOfMissingProperties = reqProperties.stream().
                filter(reqProperty -> !prop.containsKey(reqProperty)).collect(Collectors.joining(", "));
        if (!listOfMissingProperties.isEmpty())
            logging.error("Required properties missing: " + listOfMissingProperties);
        return listOfMissingProperties.isEmpty();
    }

    public static List<String> getMissingProperties(List<String> reqProperties, Properties prop) {
        return reqProperties.stream().filter(reqProperty -> !prop.containsKey(reqProperty)).collect(Collectors.toList());
    }
}
