/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 5:13:53 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.List;

/**
 * Representation of a taskdef in an Ant build script.
 *
 * <p>There are three types of taskdef formats:</p>
 *
 * <p>First, the easy one: <taskdef name="theTask" classname="com.something.theTask" classpath="${basedir}/lib/theTask.jar"/></p>
 *
 * <p>Next, getting the def from a file: <taskdef file="blash.properties"/></p>
 *
 * <p>Third, the one where the resource file is hidden somewhere in the given classpath - we're using pathelements <taskdef resource="cactus.tasks">
 * <classpath> <pathelement location="${lib.testing}/cactus-1.5.jar"/> <pathelement location="${lib.testing}/cactus-ant-1.5.jar"/> <pathelement
 * location="${lib.commons}/commons-httpclient.jar"/> <pathelement location="${lib.commons}/commons-logging.jar"/> <pathelement
 * location="${lib}/aspectjrt-1.1.1.jar"/> </classpath> </taskdef></p>
 *
 * <p>Fourth, a similar case, but we're given a classpathref instead of pathelements <taskdef resource="emma_ant.properties"
 * classpathref="emma.path"/></p>
 *
 * <p>Crap, another one! Fifth - No declared classpath - use the classpath for Ant!!! <taskdef resource="cactus.tasks"/></p>
 *
 * <p>In reality, cases 3-5 are the same as 2 - you just have to search the given classpaths instead of the given file. Hint - have a common method at
 * the core</p>
 */
public class Taskdef extends Node
{
  public Taskdef(Element element, Antfile antfile)
  {
    shape        = "hexagon";
    color        = "green";
    this.element = element;

    Attribute fileAttribute = element.getAttribute("file");
    Attribute nameAttribute = element.getAttribute("name");
    Attribute resourceAttribute = element.getAttribute("resource");

    if (fileAttribute != null)
    {
      getTaskdefFromFile(fileAttribute);
    }
    else if (nameAttribute != null)
    {
      getTaskdefFromLine(nameAttribute);
    }
    else if (resourceAttribute != null)
    {
      getTaskdefFromResource(element);
    }
    else
    {
      name = (resourceAttribute != null) ? resourceAttribute.getValue()
                                         : "UnidentifiedTask";
    }

    setAntfile(antfile);
  }

  /** Case 5: <taskdef file="blash.properties"/> */
  private void getTaskdefFromFile(Attribute fileAttribute)
  {
    // Todo
  }

  /** Case 1: <taskdef name="versionupdate" classname="com.ryangrier.ant.VersionUpdate" classpath="${basedir}/lib/version_tool.jar"/> */
  private void getTaskdefFromLine(Attribute nameAttribute)
  {
    name = nameAttribute.getValue();
  }

  /**  */
  private void getTaskdefFromResource(Element theElement)
  {
    Attribute resourceAttribute = theElement.getAttribute("resource");

    if (resourceAttribute != null)
    {
      Attribute classpathrefAttribute = theElement.getAttribute("classpathref");

      if (classpathrefAttribute != null)
      {
        getTaskdefFromClasspathref(theElement);
      }
      else
      {
        Element child = theElement.getChild("classpath");

        if (child != null)
        {
          getTaskdefFromClasspath(resourceAttribute, theElement, child);
        }
      }
    }
  }

  /**  */
  private void getTaskdefFromClasspath(Attribute resourceAttribute, Element taskdefElement, Element classpathElement)
  {
    if (resourceAttribute != null)
    {
      List      pathElements       = classpathElement.getChildren("pathelement");
      Attribute classpathAttribute = classpathElement.getAttribute("classpath");

      if (!pathElements.isEmpty())
      {
        getTaskdefFromClasspathElements(resourceAttribute, taskdefElement, classpathElement, pathElements);
      }
      else if (classpathAttribute != null)
      {
        getTaskdefFromClasspathAttribute(resourceAttribute, taskdefElement, classpathElement, classpathAttribute);
      }
    }
  }

  /** Case 2: <taskdef file="blash.properties"/> */
  private void getTaskdefFromClasspathAttribute(Attribute resourceAttribute, Element taskdefElement, Element classpathElement,
                                                Attribute classpathAttribute)
  {
    // Todo
  }

  /**
   * handles case 3: <code><taskdef resource="cactus.tasks"> <classpath> <pathelement location="${lib.testing}/cactus-1.5.jar"/> <pathelement
   * location="${lib.testing}/cactus-ant-1.5.jar"/> <pathelement location="${lib.commons}/commons-httpclient.jar"/> <pathelement
   * location="${lib.commons}/commons-logging.jar"/> <pathelement location="${lib}/aspectjrt-1.1.1.jar"/> </classpath> </taskdef></code>
   */
  private void getTaskdefFromClasspathElements(Attribute resourceAttribute, Element taskdefElement, Element classpathElement, List pathElements)
  {
    String name1 = resourceAttribute.getName();
  }

  /** Handles parsing case 4: <taskdef resource="emma_ant.properties" classpathref="emma.path"/> */
  private void getTaskdefFromClasspathref(Element element)
  {
    // Todo
  }

  @Override
  protected void setNodeType()
  {
    nodeType = NodeType.TASKDEF;
  }
}
