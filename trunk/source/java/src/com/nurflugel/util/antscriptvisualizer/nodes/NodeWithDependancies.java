/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 30, 2004 Time: 8:43:30 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.UniqueList;
import com.nurflugel.util.antscriptvisualizer.events.EventCollector;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import java.util.Iterator;
import java.util.List;

/** Representation of a node with dependencies. */
@SuppressWarnings({ "ClassReferencesSubclass", "ReturnOfCollectionOrArrayField" })
public abstract class NodeWithDependancies extends Node
{
  private List<Dependency> depends = new UniqueList<Dependency>();

  protected NodeWithDependancies(String name, Antfile antfile)
  {
    super(name, antfile);
  }
  // -------------------------- OTHER METHODS --------------------------

  /** Parse target for all instances of "antcall". */
  public void parseAntCalls(Element subElement)
  {
    for (Iterator antcallIterator = subElement.getDescendants(new ElementFilter("antcall")); antcallIterator.hasNext();)
    {
      Element antcall      = (Element) antcallIterator.next();
      String  calledTarget = antcall.getAttribute("target").getValue();
      AntCall dependancy   = new AntCall(calledTarget, this, buildFile);

      buildFile.addAntCall(dependancy);
      addDependency(dependancy);
    }
  }

  /** Add a dependency to this node. */
  public void addDependency(Dependency node)
  {
    if (!depends.contains(node))
    {
      depends.add(node);
    }
  }

  /** Parse target for all instances of "ant". */
  @SuppressWarnings({ "ResultOfObjectAllocationIgnored" })
  public void parseAnts(Element subElement, EventCollector eventCollector, List<Antfile> importsToProcess, List<Antfile> importsAlreadyProcessed)
  {
    for (Iterator antIterator = subElement.getDescendants(new ElementFilter("ant")); antIterator.hasNext();)
    {
      Element antcall = (Element) antIterator.next();

      new Antfile(antcall, eventCollector, importsAlreadyProcessed, importsToProcess, this);
    }
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public String toString()
  {
    String result = super.toString() + " {";

    for (Dependency node : depends)
    {
      result += (" " + node.getName());
    }

    return result + " }";
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

  /** Get the list of dependencies for this node. */
  public List<Dependency> getDepends()
  {
    return depends;
  }
}
