/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 5:13:53 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.ANT;

/** The representation of an Ant call. */
public class Ant extends Target
{
  private NodeType nodeType;
  private Antfile  buildFile;
  private boolean  resolved;

  public Ant(String name, Antfile antFile)
  {
    super(name, antFile);
    nodeType = ANT;
  }
  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface Dependency ---------------------
  @Override
  public boolean isResolved()
  {
    return resolved;
  }

  @Override
  public void setResolved(boolean resolved)
  {
    this.resolved = resolved;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setBuildFile(Antfile buildFile)
  {
    this.buildFile = buildFile;
  }

  // -------------------------- OTHER METHODS --------------------------
  public String getDependencyExtraInfo()
  {
    // return "[style=dotted,label=\"Ant\"]";
    // return "[style=dotted,color=red]";
    return "[color=red,style=dotted]";
  }

  protected void setNodeType(NodeType antcall)
  {
    nodeType = antcall;
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public String toString()
  {
    return super.toString() + " ::: Ant{" + "antFile='" + buildFile + "'" + "}";
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
}
