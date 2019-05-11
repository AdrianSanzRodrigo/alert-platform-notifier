package com.kschool.alertplatform.common.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

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
}
