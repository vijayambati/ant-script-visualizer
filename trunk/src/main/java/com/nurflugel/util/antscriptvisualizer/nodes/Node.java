/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:39:20 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.Preferences;
import com.nurflugel.util.antscriptvisualizer.Utility;
import org.jdom.Element;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.ANT;
import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.ANTCALL;
import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.MACRODEF;
import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.TARGET;
import static com.nurflugel.util.antscriptvisualizer.nodes.NodeType.TASKDEF;

/**
 * Base representation of a Node - almost all objects in the graph are types of nodes. Since they have so much common behavior, it made sense to have
 * a superclass. Screw the people who say inheritance is a bad idea!
 */
@SuppressWarnings({ "ProtectedField" })
public abstract class Node implements Dependency
{
  protected Antfile  buildFile;
  protected Element  element;
  protected NodeType nodeType;
  protected String   color;
  protected String   name;
  protected String   shape;
  private boolean    resolved;

  @SuppressWarnings({ "OverriddenMethodCallInConstructor", "AbstractMethodCallInConstructor" })
  protected Node()
  {
    setNodeType();
  }

  protected abstract void setNodeType();

  @SuppressWarnings({ "OverriddenMethodCallInConstructor", "AbstractMethodCallInConstructor" })
  protected Node(String name, Antfile antfile)
  {
    Map properties = antfile.getProperties();

    this.name = Utility.expandPropertyName(name, properties);
    buildFile = antfile;
    setNodeType();
  }
  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface Dependency ---------------------
  public boolean isResolved()
  {
    return resolved;
  }

  public void setResolved(boolean resolved)
  {
    this.resolved = resolved;
  }

  public String getName()
  {
    return name;
  }

  public String getColor()
  {
    return color;
  }

  /** Get any extra formatting information needed for the DOT output. */
  public String getDependencyExtraInfo()
  {
    return "";
  }
  // --------------------------------------------------------------------

  // -------------------------- OTHER METHODS --------------------------
  public String getLabel()
  {
    return name;
  }

  public boolean isDependency()
  {
    return false;
  }

  public boolean isMacrodef()
  {
    return false;
  }

  protected void setAntfile(Antfile antfile)
  {
    buildFile = antfile;
  }

  /** Write the DOT file output for this node. */
  public void writeOutput(List<String> lines, Preferences preferences) throws IOException
  {
    if (name != null)
    {
      String niceName = getNiceName();

      if (shouldPrint(preferences))
      {
        String line = "\t\t" + niceName + " [label=\"" + name + "\" shape=" + shape + " color=" + color + " ];";

        lines.add(line);
      }
    }
  }

  /** Gets a name for the node which will work with GraphViz - it' doesn't like /, ', etc. */
  public String getNiceName()
  {
    String nicename      = Utility.replaceBadChars(name).trim();
    String buildFileName = Utility.replaceBadChars(getBuildFile().getBuildFile().getAbsolutePath());

    return buildFileName + "_" + TARGET + "_" + nicename;
  }

  /** Should this node print out in the final image? */
  @SuppressWarnings({ "OverlyComplexBooleanExpression" })
  public boolean shouldPrint(Preferences preferences)
  {
    boolean isTaskdef  = (preferences.shouldShowTaskdefs() && (nodeType == TASKDEF));
    boolean istarget   = (preferences.shouldShowTargets() && (nodeType == TARGET));
    boolean isMacrodef = (preferences.shouldShowMacrodefs() && (nodeType == MACRODEF));
    boolean isAntCall  = (preferences.shouldShowAntcalls() && (nodeType == ANTCALL));
    boolean isAnt      = (preferences.shouldShowAnts() && (nodeType == ANT));

    return (isTaskdef || istarget || isMacrodef || isAnt || isAntCall);
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  @SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (!(o instanceof Node))
    {
      return false;
    }

    final Node node = (Node) o;

    if ((buildFile != null) ? (!buildFile.getBuildFile().getAbsolutePath().equals(node.buildFile.getBuildFile().getAbsolutePath()))
                            : (node.buildFile != null))
    {
      return false;
    }

    return !((name != null) ? (!name.equals(node.name))
                            : (node.name != null));
  }

  @Override
  public int hashCode()
  {
    int result = ((name != null) ? name.hashCode()
                                 : 0);

    result = (29 * result) + ((buildFile.getBuildFile().getAbsolutePath() != null) ? buildFile.getBuildFile().getAbsolutePath().hashCode()
                                                                                   : 0);

    return result;
  }

  @Override
  public String toString()
  {
    return "name='" + name + "'";
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

  /** what build file is this node in? */
  public Antfile getBuildFile()
  {
    return buildFile;
  }

  public void setBuildFile(Antfile buildFile)
  {
    this.buildFile = buildFile;
  }

  public Element getElement()
  {
    return element;
  }

  public void setElement(Element element)
  {
    this.element = element;
  }

  public String getShape()
  {
    return shape;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
