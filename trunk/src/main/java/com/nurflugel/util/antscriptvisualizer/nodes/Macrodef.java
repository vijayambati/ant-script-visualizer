/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:38:03 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import org.jdom.Element;

import org.jdom.filter.ElementFilter;

import java.util.Iterator;
import java.util.List;

/** Representation of an Ant macrodef. */
public class Macrodef extends NodeWithDependancies
{
  /**  */
  public Macrodef(String name, Antfile antfile, Element element)
  {
    super(name, antfile);
    shape        = "ellipse";
    color        = "red";
    this.element = element;
  }

  @Override
  protected void setNodeType()
  {
    nodeType = NodeType.MACRODEF;
  }

  public void parseForDependencies()
  {
    super.parseAntCalls(element);
  }

  /**
   * Parse the targets in this buildFile for any usages of the localTaskdefs in the array. If found, add them to the depends for the target they're
   * used in. todo how to deal with parallel and sequence branches
   */
  public void parseForTaskdefUsage(List<Taskdef> taskdefs)
  {
    for (Taskdef taskdef : taskdefs)
    {
      Iterator taskdefElements = element.getDescendants(new ElementFilter(taskdef.getName()));

      if (taskdefElements.hasNext())  // we found one!
      {
        addDependency(taskdef);
      }
    }
  }
}
