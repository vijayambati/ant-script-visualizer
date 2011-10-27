/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 30, 2004 Time: 8:56:35 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.AntFileParser;
import com.nurflugel.util.antscriptvisualizer.LogFactory;
import com.nurflugel.util.antscriptvisualizer.Preferences;
import com.nurflugel.util.antscriptvisualizer.UniqueList;
import com.nurflugel.util.antscriptvisualizer.Utility;
import com.nurflugel.util.antscriptvisualizer.events.Event;
import com.nurflugel.util.antscriptvisualizer.events.EventCollector;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Represention of an Ant file (typically, "build.xml"). todo - replace the properties with a Configuration object. */
@SuppressWarnings({ "ReturnOfCollectionOrArrayField", "OverlyComplexClass" })
public class Antfile
{
  private static final Logger          logger                 = LogFactory.getLogger(Antfile.class);
  private static Map<String, Property> properties             = new HashMap<String, Property>();
  private Attribute                    defaultTargetAttribute;
  private Element                      rootElement;
  private File                         buildFile;
  private List<Ant>                    ants                   = new UniqueList<Ant>();
  private List<AntCall>                antCalls               = new UniqueList<AntCall>();
  private List<Antfile>                imports                = new UniqueList<Antfile>();
  private List<Macrodef>               localMacrodefs         = new UniqueList<Macrodef>();
  private List<Target>                 targets                = new UniqueList<Target>();
  private List<Taskdef>                localTaskdefs          = new UniqueList<Taskdef>();
  private String                       projectName;

  private Antfile() {}

  public Antfile(File antFile, Map<String, Property> aproperties) throws IOException, JDOMException
  {
    buildFile  = antFile;
    properties = aproperties;  // todo why am I doing this?

    if (logger.isDebugEnabled())
    {
      logger.debug("Trying to open " + buildFile);
    }

    // todo if buildfile is composed of a property, try to resolve the property
    String name = buildFile.getAbsolutePath();

    if (isValueAProperty(name))
    {
      buildFile = new File(Utility.expandPropertyName(name, properties));
    }

    SAXBuilder builder     = new SAXBuilder();
    Document   doc         = builder.build(buildFile);

    rootElement            = doc.getRootElement();
    defaultTargetAttribute = rootElement.getAttribute("default");
    parseBasedir();
    parseProperties();
    parseName();
  }

  /** Does this name include a property? */
  private boolean isValueAProperty(String name)
  {
    int openIndex = name.indexOf("${");

    return openIndex > -1;
  }

  private void parseBasedir()
  {
    Attribute basedirAttribute = rootElement.getAttribute("basedir");
    String    basedir          = ".";

    if (basedirAttribute != null)
    {
      basedir = basedirAttribute.getValue();
    }

    Property property = new Property("basedir", basedir);

    properties.put(property.getName(), property);
  }

  /** Parse any properties in this Ant file. */
  private void parseProperties()
  {
    List children = rootElement.getChildren("property");

    for (Object aChildren : children)
    {
      Element  child    = (Element) aChildren;
      Property property = new Property(child, properties, this);
    }
  }

  private void parseName()
  {
    Attribute nameAttribute = rootElement.getAttribute("name");

    if (nameAttribute != null)
    {
      projectName = nameAttribute.getValue();
    }
  }

  public Antfile(Element antcall, EventCollector eventCollector, List<Antfile> importsAlreadyProcessed, List<Antfile> importsToProcess,
                 NodeWithDependancies sourceTarget)
  {
    String  filename     = null;
    Antfile theBuildFile = sourceTarget.getBuildFile();

    try
    {
      Attribute fileAttribute = antcall.getAttribute("antfile");
      Antfile   antfile;

      if (fileAttribute != null)
      {
        filename = fileAttribute.getValue();

        Attribute dirAttribute = antcall.getAttribute("dir");
        File      theFile;

        if (dirAttribute == null)
        {
          theFile = new File(theBuildFile.getBuildFile().getParent(), filename);
        }
        else
        {
          String dirName = dirAttribute.getValue();

          theFile = new File(new File(dirName), filename);
        }

        antfile = new Antfile(theFile, theBuildFile.getProperties());

        if (!importsAlreadyProcessed.contains(antfile) && !importsToProcess.contains(antfile))
        {
          importsToProcess.add(antfile);
        }
      }
      else
      {
        antfile = theBuildFile;
      }

      Attribute targetAttribute = antcall.getAttribute("target");
      String    calledTarget;

      if (targetAttribute == null)  // no target specified, use default
      {
        calledTarget = antfile.getDefaultTargetName();
      }
      else
      {
        calledTarget = targetAttribute.getValue();
      }

      Ant dependancy = new Ant(calledTarget, antfile);

      theBuildFile.addAnt(dependancy);
      sourceTarget.addDependency(dependancy);
    }
    catch (Exception e)
    {
      logger.error("Ouch!", e);
      eventCollector.addEvent(new Event("Error parsing ant task on file " + filename, e));
    }
  }

  /** Get the default target name if it exists. Else, return an empty string. */
  public String getDefaultTargetName()
  {
    if (defaultTargetAttribute != null)
    {
      return defaultTargetAttribute.getValue();
    }
    else
    {
      return "";
    }
  }

  /** Add a target to the list of targest in this ant buildFile. */
  public void addAnt(Ant ant)
  {
    if (!ants.contains(ant))
    {
      ants.add(ant);
    }
  }
  // --------------------------- main() method ---------------------------

  /**  */
  public static void main(String[] args)
  {
    Antfile               antFile = new Antfile();
    Map<String, Property> props   = new HashMap<String, Property>();

    props.put("dibble", new Property("dibble", "dibbleValue"));
    antFile.setProperties(props);

    String result  = Utility.expandPropertyName("doug${dibble}bruce", props);
    String result2 = Utility.expandPropertyName("${dibble}bruce", props);
    String result3 = Utility.expandPropertyName("doug${dibble}", props);
  }
  // -------------------------- OTHER METHODS --------------------------

  // ------------------------ OTHER METHODS ------------------------
  /** Add an import to the list of targest in this ant buildFile. */
  public void addImport(Antfile importedAntfile)
  {
    if (!imports.contains(importedAntfile))
    {
      imports.add(importedAntfile);
    }
  }

  /** Get a nicely formatted file name for GraphViz. */
  public String getNiceName()
  {
    String name = getBuildFile().getName();

    return Utility.replaceBadChars(name);
  }

  /**
   * Parse this file.
   *
   * @param  parser                   the parser which will be used to parse this file.
   * @param  importsToProcess         The list of imports which need to be processed.
   * @param  importsAlreadyProcessed  the list of imports which have already been processed.
   * @param  preferences
   */
  public void parse(AntFileParser parser, List<Antfile> importsToProcess, List<Antfile> importsAlreadyProcessed, EventCollector eventCollector,
                    Preferences preferences) throws IOException, JDOMException
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("processing " + buildFile);
    }

    parseImports(parser, importsToProcess, importsAlreadyProcessed, eventCollector);
    parseTargets(eventCollector, importsToProcess, importsAlreadyProcessed, preferences);
  }

  /** Get any imports from this ant file. */
  private void parseImports(AntFileParser parser, List<Antfile> importsToProcess, List<Antfile> importsAlreadyProcessed,
                            EventCollector eventCollector)
  {
    if (parser.includeImportedFiles())
    {
      Iterator descendants = rootElement.getDescendants(new ElementFilter("import"));

      while (descendants.hasNext())
      {
        Element   element          = (Element) descendants.next();
        Attribute elementAttribute = element.getAttribute("file");
        String    importName       = elementAttribute.getValue().trim();

        importName = Utility.expandPropertyName(importName, properties);

        Antfile importedAntfile = null;

        try
        {
          importedAntfile = new Antfile(new File(buildFile.getParent(), importName), properties);
        }
        catch (IOException e)
        {
          eventCollector.addEvent(new Event("Error adding ant file", e));
          logger.error(e);
        }
        catch (JDOMException e)
        {
          eventCollector.addEvent(new Event("Error adding ant file", e));
          logger.error(e);
        }

        if (!importsAlreadyProcessed.contains(importedAntfile) && !importsToProcess.contains(importedAntfile))
        {
          importsToProcess.add(importedAntfile);
        }
      }
    }
  }

  /** Parse the targets in this build file. */
  private void parseTargets(EventCollector eventCollector, List<Antfile> importsToProcess, List<Antfile> importsAlreadyProcessed,
                            Preferences preferences)
  {
    Iterator descendants = rootElement.getDescendants(new ElementFilter("target"));

    while (descendants.hasNext())
    {
      Element   targetElement      = (Element) descendants.next();
      String    targetName         = targetElement.getAttribute("name").getValue().trim();
      Attribute descriptionElement = targetElement.getAttribute("description");
      String    description        = (descriptionElement != null) ? descriptionElement.getValue().trim()  // todo replace with StringUtils
                                                                  : "";
      Target target = new Target(targetName, description, this, targetElement);                 // these could be in an imported ant buildFile -
                                                                                                // need to find them, maybe later

      addTarget(target);
      target.parseDepends();

      if (preferences.shouldShowAntcalls())
      {
        target.parseAntCalls(targetElement);
      }

      if (preferences.shouldShowAnts())
      {
        target.parseAnts(targetElement, eventCollector, importsToProcess, importsAlreadyProcessed);
      }
    }
  }

  /** Add a target to the list of targest in this ant buildFile. */
  public void addTarget(Target target)
  {
    if (!targets.contains(target))
    {
      targets.add(target);
    }
  }

  /** Find all "antcall" usage. */
  public void parseForAntcalls()
  {
    for (Target calledFromTarget : targets)
    {
      for (Iterator targetsIterator = rootElement.getDescendants(new ElementFilter("calledFromTarget")); targetsIterator.hasNext();)
      {
        Element targetElement = (Element) targetsIterator.next();

        if (targetElement.getAttribute("name").getValue().equalsIgnoreCase(calledFromTarget.getName()))                // parse this calledFromTarget
                                                                                                                       // for usages of "ant"
        {
          for (Iterator antIterator = targetElement.getDescendants(new ElementFilter("ant")); antIterator.hasNext();)  // we found one!
          {
            Element antElement   = (Element) antIterator.next();
            String  antfile      = buildFile.getAbsolutePath();                                                        // default value if they don't
                                                                                                                       // specify an antfile, then
                                                                                                                       // THIS is the
                                                                                                                       // antElement buildFile
            String  calledTarget = antElement.getAttribute("calledFromTarget").getValue();
            AntCall antCall      = new AntCall(calledTarget, calledFromTarget, this);

            addAntCall(antCall);
          }
        }
      }
    }
  }

  /** Add a target to the list of targest in this ant buildFile. */
  public void addAntCall(AntCall antCall)
  {
    if (!antCalls.contains(antCall))
    {
      antCalls.add(antCall);
    }
  }

  /** Get a list of any "Ant" task usage in the ant buildFile. */
  public List<Antfile> parseForAnts(EventCollector eventCollector) throws IOException, JDOMException
  {
    List<Antfile> antfiles = new UniqueList<Antfile>();

    for (Target target : targets)
    {
      for (Iterator targetsIterator = rootElement.getDescendants(new ElementFilter("target")); targetsIterator.hasNext();)
      {
        Element targetElement = (Element) targetsIterator.next();

        if (targetElement.getAttribute("name").getValue().equalsIgnoreCase(target.getName()))                          // parse this target for usages
                                                                                                                       // of "ant"
        {
          for (Iterator antIterator = targetElement.getDescendants(new ElementFilter("ant")); antIterator.hasNext();)  // we found one!
          {
            parseAnt(antIterator, antfiles, eventCollector);
          }
        }
      }
    }

    return antfiles;
  }

  /** todo - put this into the Ant object's contsturctor. */
  private void parseAnt(Iterator antIterator, List<Antfile> antfiles, EventCollector eventCollector)
  {
    try
    {
      Element   antElement = (Element) antIterator.next();
      String    antfile    = buildFile.getName();  // default value if they don't specify an antfile, then THIS is the
                                                   // antElement buildFile
      String    antParent  = buildFile.getParent();
      Attribute attribute  = antElement.getAttribute("antfile");

      if (attribute != null)
      {
        antfile = attribute.getValue();
      }

      String    antdir          = null;
      Attribute antDirAttribute = antElement.getAttribute("dir");

      if (antDirAttribute != null)
      {
        antdir = antDirAttribute.getValue();
      }

      Antfile antcallfile;

      if (antdir == null)
      {
        antcallfile = new Antfile(new File(antParent, antfile), properties);
      }
      else
      {
        antcallfile = new Antfile(new File(antdir, antfile), properties);
      }

      antfiles.add(antcallfile);

      Attribute targetAttribute = antElement.getAttribute("target");

      // todo what if this is null?  Will use the default target of the ant file - must code
      Target defaultTarget = antcallfile.getDefaultTarget();

      if (defaultTarget != null)
      {
        String defaultTargetName = defaultTarget.getName();
        String calledTarget;

        if (targetAttribute == null)
        {
          calledTarget = defaultTargetName;
        }
        else
        {
          calledTarget = targetAttribute.getValue();
        }

        Ant ant = new Ant(calledTarget, antcallfile);

        ants.add(ant);
      }
    }
    catch (IOException e)
    {
      eventCollector.addEvent(new Event("Error pasring Ant file", e));
    }
    catch (JDOMException e)
    {
      eventCollector.addEvent(new Event("Error pasring Ant file", e));
    }
  }

  /** Get the efault target, if any. todo - this return null is crap */
  private Target getDefaultTarget()
  {
    if (defaultTargetAttribute != null)
    {
      return new Target(defaultTargetAttribute.getValue(), this);
    }
    else
    {
      return null;
    }
  }

  /**
   * Parse the targets in this buildFile for any usages of the localTaskdefs in the array. If found, add them to the depends for the target they're
   * used in. todo how to deal with parallel and sequence branches
   */
  public void parseForMacrodefUsage(List<Macrodef> macrodefs)
  {
    for (Iterator targetElements = rootElement.getDescendants(new ElementFilter("target")); targetElements.hasNext();)
    {
      Element targetElement = (Element) targetElements.next();
      Target  target        = getMatchingTarget(targetElement);

      for (Macrodef macrodef : macrodefs)
      {
        Iterator macrodefElements = targetElement.getDescendants(new ElementFilter(macrodef.getName()));

        if (macrodefElements.hasNext())  // we found one!
        {
          target.addDependency(macrodef);
        }
      }
    }

    for (Iterator elements = rootElement.getDescendants(new ElementFilter("macrodef")); elements.hasNext();)
    {
      Element  targetElement    = (Element) elements.next();
      Macrodef matchingMacrodef = getMatchingMacrodef(targetElement);

      for (Node macrodef : macrodefs)
      {
        Iterator macrodefElements = targetElement.getDescendants(new ElementFilter(macrodef.getName()));

        if (macrodefElements.hasNext())  // we found one!
        {
          matchingMacrodef.addDependency(macrodef);
        }
      }
    }
  }

  /** Return the Target maching the given target element. */
  private Target getMatchingTarget(Element targetElement)
  {
    String targetName = targetElement.getAttribute("name").getValue();

    for (Target target : targets)
    {
      String label = target.getName();

      if (label.equals(targetName))
      {
        return target;
      }
    }

    return null;
  }

  /** Return the Target maching the given target element. */
  private Macrodef getMatchingMacrodef(Element targetElement)
  {
    String targetName = targetElement.getAttribute("name").getValue();

    for (Macrodef macrodef : localMacrodefs)
    {
      String label = macrodef.getName();

      if (label.equals(targetName))
      {
        return macrodef;
      }
    }

    return null;
  }

  /** find any localMacrodefs in this ant buildFile. */
  public List<Macrodef> parseForMacrodefs()
  {
    parseElementForMacrodefs(rootElement);      // parse outside of targets - most common definition

    for (Iterator targetsIterator = rootElement.getDescendants(new ElementFilter("target")); targetsIterator.hasNext();)
    {
      Element targetElement = (Element) targetsIterator.next();

      parseElementForMacrodefs(targetElement);  // parse inside of targets (such as "init"
    }

    return localMacrodefs;
  }

  /** find any localMacrodefs in the given element. */
  private void parseElementForMacrodefs(Element element)
  {
    for (Iterator targetsIterator = element.getDescendants(new ElementFilter("macrodef")); targetsIterator.hasNext();)
    {
      Element   macrodefElement = (Element) targetsIterator.next();
      Attribute nameAttribute   = macrodefElement.getAttribute("name");
      String    name            = nameAttribute.getValue();
      Macrodef  macrodef        = new Macrodef(name, this, macrodefElement);

      addMacrodef(macrodef);
      macrodef.parseForDependencies();  // dgbtodo -elsewhere???
    }
  }

  /** add a macrodef to the list of macrodefs for this file. */
  public void addMacrodef(Macrodef macrodef)
  {
    if (!localMacrodefs.contains(macrodef))
    {
      localMacrodefs.add(macrodef);
    }
  }

  /**
   * Parse the targets in this buildFile for any usages of the localTaskdefs in the array. If found, add them to the depends for the target they're
   * used in. todo how to deal with parallel and sequence branches
   */
  public void parseForTaskdefUsage(List<Taskdef> taskdefs)
  {
    for (Iterator targetElements = rootElement.getDescendants(new ElementFilter("target")); targetElements.hasNext();)
    {
      Element targetElement = (Element) targetElements.next();
      Target  target        = getMatchingTarget(targetElement);

      for (Taskdef taskdef : taskdefs)
      {
        Iterator taskdefElements = targetElement.getDescendants(new ElementFilter(taskdef.getName()));

        if (taskdefElements.hasNext())  // we found one!
        {
          target.addDependency(taskdef);
        }
      }
    }
  }

  /** Find any taskdef definitions. */
  public List<Taskdef> parseForTaskdefs()
  {
    parseElementForTaskdefs(rootElement);      // parse outside of targets - most common definition

    for (Iterator targetsIterator = rootElement.getDescendants(new ElementFilter("target")); targetsIterator.hasNext();)
    {
      Element targetElement = (Element) targetsIterator.next();

      parseElementForTaskdefs(targetElement);  // parse inside of targets (such as "init"
    }

    return localTaskdefs;
  }

  /** Look at the specific element for localTaskdefs, add them to the list. */
  private void parseElementForTaskdefs(Element element)
  {
    for (Iterator targetsIterator = element.getDescendants(new ElementFilter("taskdef")); targetsIterator.hasNext();)
    {
      Element taskdefElement = (Element) targetsIterator.next();
      Taskdef taskdef        = new Taskdef(taskdefElement, this);

      addTaskdef(taskdef);
    }
  }

  /** Add a target to the list of targest in this ant buildFile. */
  public void addTaskdef(Taskdef taskdef)
  {
    if (!localTaskdefs.contains(taskdef))
    {
      localTaskdefs.add(taskdef);
    }
  }

  /**  */
  private void parseUrlForProperties(Element child) {}

  /** Resolve any leftover dependencies with any matching external targets. */
  public void resolveExternalDependencies(List<Antfile> antfiles)
  {
    List<Dependency> unresolvedDependencies = getUnresolvedDependencies();

    for (Dependency unresolvedDependency : unresolvedDependencies)
    {
      boolean found                    = false;
      String  unresolvedDependencyName = unresolvedDependency.getName();

      for (Antfile antfile : antfiles)
      {
        if (!antfile.getBuildFile().equals(buildFile))
        {
          List<Target> targets = getAllTargets(antfile);

          for (Target target : targets)
          {
            String targetName          = target.getName();
            String qualifiedTargetname = antfile.getProjectName() + "." + targetName;

            if (unresolvedDependencyName.equals(targetName) || unresolvedDependencyName.equals(qualifiedTargetname))
            {
              unresolvedDependency.setResolved(true);
              unresolvedDependency.setBuildFile(antfile);
              found = true;

              break;
            }

            if (found)
            {
              break;
            }
          }
        }
      }
    }  // end for
  }

  /**  */
  private List<Dependency> getUnresolvedDependencies()
  {
    List<Dependency> unresolvedDepends = new ArrayList<Dependency>();
    List<Target>     allTargets        = getAllTargets(this);

    for (Target target : allTargets)
    {
      List<Dependency> depends = target.getDepends();

      for (Dependency depend : depends)
      {
        if (!depend.isResolved())
        {
          unresolvedDepends.add(depend);
        }
      }
    }

    return unresolvedDepends;
  }

  /**  */
  private List<Target> getAllTargets(Antfile antfile)
  {
    List<Target>  fileTargets  = antfile.getTargets();
    List<AntCall> fileAntcalls = antfile.getAntCalls();
    List<Target>  allTargets   = new ArrayList<Target>();

    allTargets.addAll(fileTargets);
    allTargets.addAll(fileAntcalls);

    return allTargets;
  }

  /** Resolve any dependencies with internal targets. */
  public void resolveInternalDependencies()
  {
    List<Dependency> dependencies = getUnresolvedDependencies();

    for (Dependency dependency : dependencies)
    {
      List<Target> allTargets = getTargets();

      for (Target target : allTargets)
      {
        String dependencyName = dependency.getName();
        String targetName     = target.getName();

        System.out.println("Antfile.resolveInternalDependencies dependencyName, targetName = " + dependencyName + " " + targetName);

        if (dependencyName == null)
        {
          System.out.println("Antfile.resolveInternalDependencies dependency = " + dependency);
        }
        else if (dependencyName.equals(targetName))
        {
          dependency.setResolved(true);

          break;
        }
      }
    }
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (!(o instanceof Antfile))
    {
      return false;
    }

    final Antfile antfile = (Antfile) o;

    return buildFile.getAbsolutePath().equals(antfile.buildFile.getAbsolutePath());
  }

  @Override
  public int hashCode()
  {
    return buildFile.getAbsolutePath().hashCode();
  }

  /**  */
  @Override
  public String toString()
  {
    return buildFile.getName();
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

  /** Return the antcalls for this build file. */
  public List<AntCall> getAntCalls()
  {
    return antCalls;
  }

  /** Return the ant calls for this build file. */
  public List<Ant> getAnts()
  {
    return ants;
  }

  /** Return the actual File. */
  public File getBuildFile()
  {
    return buildFile;
  }

  /** Any imports used in this build file. */
  public List<Antfile> getImports()
  {
    return imports;
  }

  /** Any macrodefs used in this build file. */
  public List<Macrodef> getLocalMacrodefs()
  {
    return localMacrodefs;
  }

  /** Any imports used in this build file. */
  public List<Taskdef> getLocalTaskdefs()
  {
    return localTaskdefs;
  }

  public String getProjectName()
  {
    return projectName;
  }

  /** Any properties in this build file. */
  public Map<String, Property> getProperties()
  {
    return properties;
  }

  /**  */
  private void setProperties(Map<String, Property> props)
  {
    properties = props;
  }

  /** Any targets used in this build file. */
  public List<Target> getTargets()
  {
    return targets;
  }
}
