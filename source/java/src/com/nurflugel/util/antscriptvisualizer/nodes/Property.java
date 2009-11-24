package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.LogFactory;
import com.nurflugel.util.antscriptvisualizer.Utility;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import java.io.*;
import java.util.*;

/** Representation a a Property in Ant. */
public class Property
{
  private static final Logger logger = LogFactory.getLogger(Property.class);

  private String              name;
  private String              value;

  public Property(String name, String value)
  {
    this.name  = name;
    this.value = value;
  }

  public Property(Element element, Map<String, Property> properties, Antfile antfile)
  {
    // check to see if
    // they're reading in properties from a file,
    // or if they're declared,
    // or it's environment variables
    try
    {
      parseNameForProperty(element, properties);
      parseResourceForProperties(element);
      parseFileForProperties(element, properties, antfile);
      parseUrlForProperties(element, properties);
      parseEnvironmentForProperties(element, properties);
    }
    catch (Exception e)
    {
      logger.error("Something bad happened", e);
    }
  }

  /** Look through the environment for any properties. */
  private void parseEnvironmentForProperties(Element child, Map<String, Property> properties)
  {
    Attribute envAttribute = child.getAttribute("environment");

    // To change body of created methods use File | Settings | File Templates.
    if (envAttribute != null)
    {
      String      envName          = envAttribute.getValue();
      Properties  systemProperties = System.getProperties();
      Set<Object> set              = systemProperties.keySet();

      for (Object aSet : set)
      {
        String   key      = (String) aSet;
        String   envValue = systemProperties.getProperty(key);
        Property property = new Property(envName + "." + key, envValue);

        properties.put(property.getName(), property);
        logger.debug("Adding " + property + " to properties");
      }
    }
  }

  /** Look through the file for any properties. */
  @SuppressWarnings({ "OverlyBroadCatchBlock" })
  private void parseFileForProperties(Element child, Map<String, Property> properties, Antfile antfile)
  {
    Attribute      fileAttribute        = child.getAttribute("file");
    List<Property> unresolvedProperties = new ArrayList<Property>();

    if (fileAttribute != null)
    {
      BufferedReader input = null;

      try
      {
        String fileName = Utility.expandPropertyName(fileAttribute.getValue(), properties);
        File   file1    = new File(antfile.getBuildFile().getParent(), fileName);
        File   file2    = new File(fileName);
        File   file     = file1.exists() ? file1
                                         : file2;  // lame hack - replace basedir with . later on to fix???

        if (file.exists())
        {
          input = new BufferedReader(new FileReader(file));

          String line;
          int    lineNumber = 0;

          while ((line = input.readLine()) != null)
          {
            lineNumber = processLine(lineNumber, fileName, line, properties, unresolvedProperties);
          }
        }

        logger.debug("done with reading file " + fileName);
      }
      catch (FileNotFoundException e)
      {                                            // logger.error("Error finding file ", e);
                                                   // capture the event
      }
      catch (IOException e)
      {
        logger.error("Error parsing file", e);
      }
      catch (Exception e)
      {
        logger.error("bad juju", e);
      }
      finally
      {
        try
        {
          for (Property unresolvedProperty : unresolvedProperties)
          {
            String avalue = unresolvedProperty.getValue();

            avalue = Utility.expandPropertyName(avalue, properties);
            unresolvedProperty.setValue(avalue);
          }

          if (input != null)
          {
            input.close();
          }
        }
        catch (Exception e)
        {
          logger.debug("Yeah, yeah", e);
        }
      }
    }                                              // end if
  }

  /** Processes the line looking for properies. */
  private int processLine(int lineNumber, String fileName, String line, Map<String, Property> properties, List<Property> unresolvedProperties)
  {
    logger.debug("Reading in line " + lineNumber++ + "of file " + fileName + ": " + line);

    if ((line.trim().length() > 0) && !line.startsWith("#") && !line.startsWith("--"))
    {
      String[] strings = line.split("=");

      String   key     = strings[0];
      String   keyValue = (strings.length > 1) ? strings[1]
                                               : "";  // default to empty if no value defined.

      String originalValue = keyValue;

      keyValue = Utility.expandPropertyName(keyValue, properties);

      Property property = new Property(key, keyValue);

      properties.put(property.getName(), property);

      if (keyValue.contains("${"))                    // still has unresolved properties (lines out of order)
      {
        logger.debug("Adding unresolved " + keyValue + " to be resolved later");
        unresolvedProperties.add(property);
      }
      else
      {
        logger.debug("Resolved " + originalValue + " to " + keyValue);
      }
    }

    return lineNumber;
  }

  /** See if the given name has a buried property - like {build.dir}/myfile - adn resolve it. */
  private void parseNameForProperty(Element child, Map<String, Property> properties)
  {
    Attribute nameAttribute = child.getAttribute("name");

    if (nameAttribute != null)
    {
      Attribute valueAttribute = child.getAttribute("value");
      String    theName        = nameAttribute.getValue();
      String    theValue       = valueAttribute.getValue();

      Property  property       = new Property(theName, theValue);

      properties.put(property.getName(), property);
    }
  }

  /**  */
  private void parseResourceForProperties(Element child) {}

  /**  */
  private void parseUrlForProperties(Element element, Map<String, Property> properties) {}

  @Override
  public String toString()
  {
    return name + "::" + value;
  }

  public String getName()
  {
    return name;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}
