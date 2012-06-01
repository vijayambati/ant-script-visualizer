package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import com.nurflugel.util.antscriptvisualizer.nodes.Node;
import com.nurflugel.util.antscriptvisualizer.nodes.NodeType;
import com.nurflugel.util.antscriptvisualizer.nodes.PathElement;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Mar 15, 2007 Time: 11:52:23 PM To change this template use File | Settings | File Templates.
 */
// <path id="base.path">
// <pathelement path="${classpath}"/>
// <fileset dir="lib">
// <include name="**/*.jar"/>
// </fileset>
// <pathelement location="classes"/>
// </path>
//
// <path id="tests.path">
// <path refid="base.path"/>
// <pathelement location="testclasses"/>
// </path>
public class Path extends Node
{
  private List<Fileset>     filesets     = new ArrayList<Fileset>();
  private List<PathElement> pathElements = new ArrayList<PathElement>();

  public Path(Element element)
  {
    shape        = "hexagon";
    color        = "green";
    this.element = element;

    Attribute attribute = element.getAttribute("id");
    String    id = attribute.getValue();
  }

  @Override
  protected void setNodeType()
  {
    nodeType = NodeType.PATH;
  }
}
