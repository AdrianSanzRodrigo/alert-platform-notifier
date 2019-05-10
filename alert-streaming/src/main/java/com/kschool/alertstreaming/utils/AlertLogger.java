package com.kschool.alertstreaming.utils;

import org.apache.log4j.Logger;

public class AlertLogger {

    private Logger logger;

    public AlertLogger(String className) {
        logger = Logger.getLogger(className);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Exception e) { logger.warn(msg, e);}

    public void error(String msg) { logger.error(msg); }

    public void error(String msg, Exception e) { logger.error(msg, e);}

}
