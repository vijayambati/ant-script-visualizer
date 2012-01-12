package com.nurflugel.util.antscriptvisualizer;

import com.nurflugel.util.antscriptvisualizer.nodes.Property;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.util.Map;
import static org.apache.commons.lang.StringUtils.replace;

/** Evil utility class. */
public class Utility
{
  private static final Logger logger = LogFactory.getLogger(Utility.class);

  /** Creates a new Utility object. */
  private Utility() {}

  /** Expand the string out with property substitution. */
  public static String expandPropertyName(String name, Map properties)
  {
    String modifiedName = name.trim();
    int    openIndex    = name.indexOf("${");

    try
    {
      while (openIndex > -1)
      {
        int    endIndex     = modifiedName.indexOf("}");
        String beginning    = modifiedName.substring(0, openIndex);
        String propertyName = modifiedName.substring(openIndex + 2, endIndex);
        String end          = (endIndex <= modifiedName.length()) ? modifiedName.substring(endIndex + 1)
                                                                  : "";
        Property property = (Property) properties.get(propertyName);

        if (property != null)
        {
          String value = property.getValue();

          modifiedName = beginning + value + end;
        }

        openIndex = modifiedName.indexOf("${", openIndex + 1);
      }
    }
    catch (Exception e)
    {
      logger.error("dibble", e);
    }

    return modifiedName;
  }

  /**  */
  public static String replaceBadChars(String oldValue)
  {
    String newValue = replace(oldValue, "-", "_");

    newValue = replace(newValue, ".xml", "");
    newValue = replace(newValue, " ", "_");
    newValue = replace(newValue, "'", "_");
    newValue = replace(newValue, ":", "_");
    newValue = replace(newValue, ".", "_");
    newValue = replace(newValue, "/", "_");
    newValue = replace(newValue, "\\", "_");

    return newValue;
  }
}
