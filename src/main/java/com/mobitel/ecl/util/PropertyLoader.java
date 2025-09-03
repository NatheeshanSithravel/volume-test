package com.mobitel.ecl.util;

import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyLoader {

    //    private static final String ECL_PROPERTY = "/apps/mobitel/ECL/ecl.properties"; - modified to below on 2021/03/05
    private static final String ECL_PROPERTY = "/logs/ecl/config/ecl.properties";
    //    private static final String ECL_PROPERTY = "/home/ganeeshad/ECLReport/ecl.properties";
//    private static final String ECL_PROPERTY = "D:\\Office\\Projects\\Task11 - ECL for Mobitel Branches\\props\\ecl.properties";
    private static Properties ECLProp;


    public static Properties getECLProperties(Logger logger) {
        try {

            ECLProp = new Properties();
            ECLProp.load(new FileInputStream(ECL_PROPERTY));
       } catch (Exception e) {
            logger.error("PropertyLoader.getECLProperties Error | "+e);
        }
        return ECLProp;
    }
}
