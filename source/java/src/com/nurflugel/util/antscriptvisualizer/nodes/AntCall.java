/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 5:13:53 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

/** The representation of an Antcall call. */
public class AntCall extends Ant
{
  private NodeWithDependancies calledFromTarget;

  public AntCall(String targetName, NodeWithDependancies calledFromTarget, Antfile buildfile)
  {
    super(targetName, buildfile);
    shape                 = "hexagon";
    color                 = "blue";
    this.calledFromTarget = calledFromTarget;
  }

  @Override
  @SuppressWarnings({ "RefusedBequest" })
  public String getDependencyExtraInfo()
  {
    return "[color=green,style=dotted]";
  }

  @Override
  @SuppressWarnings({ "RefusedBequest" })
  protected void setNodeType()
  {
    nodeType = NodeType.ANTCALL;
  }

  public NodeWithDependancies getCalledFromTarget()
  {
    return calledFromTarget;
  }
}
