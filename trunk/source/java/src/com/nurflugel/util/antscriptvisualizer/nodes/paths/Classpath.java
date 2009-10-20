package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import com.nurflugel.util.antscriptvisualizer.nodes.NodeType;
import org.jdom.Attribute;
import org.jdom.Element;

// <classpath>
// <pathelement path="${classpath}"/>
// <pathelement location="lib/helper.jar"/>
// </classpath>
//
//
// <classpath>
// <pathelement path="${classpath}"/>
// <fileset dir="lib">
// <include name="**/*.jar"/>
// </fileset>
// <pathelement location="classes"/>
// <dirset dir="build">
// <include name="apps/**/classes"/>
// <exclude name="apps/**/*Test*"/>
// </dirset>
// <filelist refid="third-party_jars"/>
// </classpath>

/**
 * The representation of a classpath in Ant. Although this strictly isn't a node (graphic node) at this time, I might want to visually display it in
 * the future, so I'm extending Node.
 */
public class Classpath extends Path
{
  public Classpath(Element element)
  {
    super(element);
    shape = "rectangle";
    color = "blue";

    Attribute attribute = element.getAttribute("id");
    String    id = attribute.getValue();
  }

  @Override
  protected void setNodeType()
  {
    nodeType = NodeType.CLASSPATH;
  }
}
