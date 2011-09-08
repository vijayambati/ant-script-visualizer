/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:38:03 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.LogFactory;

import org.apache.log4j.Logger;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.List;

/** Representation of a target in a build script. */
public class Target extends NodeWithDependancies
{
  private static final Logger logger      = LogFactory.getLogger(Target.class);
  private String              description = "";

  public Target(String name, Antfile antfile)
  {
    super(name, antfile);
    shape = "box";
    color = "black";
  }

  public Target(String name, String description, Antfile antfile)
  {
    this(name, antfile);
    this.description = description;
  }

  public Target(String targetName, String description, Antfile antfile, Element targetElement)
  {
    this(targetName, description, antfile);
    setElement(targetElement);
  }

  @Override
  @SuppressWarnings({ "RefusedBequest" })
  public String getLabel()
  {
    return description;
  }

  @Override
  protected void setNodeType()
  {
    nodeType = NodeType.TARGET;
  }

  /** Find all the "depends" for this target. */
  @SuppressWarnings({ "ClassReferencesSubclass" })
  public void parseDepends()
  {
    List attributes = element.getAttributes();

    for (Object attribute1 : attributes)
    {
      Attribute attribute     = (Attribute) attribute1;
      String    attributeName = attribute.getName();

      if ("depends".equalsIgnoreCase(attributeName))
      {
        String   value   = attribute.getValue();
        String[] strings = value.split(",");

        for (String text : strings)
        {
          String dependsName = text.trim();

          if ((dependsName != null) && (dependsName.length() > 0))
          {
            Dependency dependency = new Target(dependsName, buildFile);

            addDependency(dependency);
          }
        }
      }
    }
  }
}
