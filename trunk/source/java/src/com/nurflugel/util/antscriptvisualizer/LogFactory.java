package com.nurflugel.util.antscriptvisualizer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.net.URL;

/** Loader for log4j. */
@SuppressWarnings({ "UseOfSystemOutOrSystemErr" })
public class LogFactory
{
  static
  {
    try
    {
      ClassLoader classLoader = LogFactory.class.getClassLoader();
      URL         resource    = classLoader.getResource("conf/log4j.prop");

      System.out.println("resource = " + resource);

      final URL resource2 = classLoader.getResource("log4j.prop");

      System.out.println("resource2 = " + resource2);

      PropertyConfigurator.configure(resource2);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /** Creates a new LogFactory object. */
  private LogFactory() {}

  /**  */
  public static Logger getLogger(final Class daClass)
  {
      Logger logger = Logger.getLogger(daClass);
      return logger;
  }
}
