package com.nurflugel.util.antscriptvisualizer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;


/** Loader for log4j. */
@SuppressWarnings({ "UseOfSystemOutOrSystemErr" })
public class LogFactory
{

    private static boolean configured = false;
    private static Logger  instance;

    /** Creates a new LogFactory object. */
    private LogFactory() { }

    /**  */
    public static Logger getLogger(final Class daClass)
    {

        synchronized (LogFactory.class) {

            if (!configured) {
                final ClassLoader classLoader = LogFactory.class.getClassLoader();
                final URL         resource    = classLoader.getResource("conf/log4j.prop");

                System.out.println("resource = " + resource);

                final URL resource2 = classLoader.getResource("log4j.prop");

                System.out.println("resource2 = " + resource2);

                PropertyConfigurator.configure(resource2);

                configured = true;
            }
        }

        instance = Logger.getLogger(daClass);

        return instance;
    }
}
