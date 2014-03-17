/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 25, 2004 Time: 8:04:03 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import com.nurflugel.util.Os;
import com.nurflugel.util.antscriptvisualizer.events.Event;
import com.nurflugel.util.antscriptvisualizer.events.EventCollector;
import com.nurflugel.util.antscriptvisualizer.events.GenericException;
import com.nurflugel.util.antscriptvisualizer.nodes.Antfile;
import com.nurflugel.util.antscriptvisualizer.nodes.Dependency;
import com.nurflugel.util.antscriptvisualizer.nodes.Macrodef;
import com.nurflugel.util.antscriptvisualizer.nodes.Node;
import com.nurflugel.util.antscriptvisualizer.nodes.Property;
import com.nurflugel.util.antscriptvisualizer.nodes.Target;
import com.nurflugel.util.antscriptvisualizer.nodes.Taskdef;
import com.nurflugel.util.antscriptvisualizer.nodes.paths.Classpath;
import com.nurflugel.util.antscriptvisualizer.nodes.paths.Path;

import org.apache.log4j.Logger;

import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * This class is involved in parsing the Ant file and generating the DOT output.
 *
 * <p>Sounds like it's time to refactor, eh?</p>
 */
@SuppressWarnings({ "CallToRuntimeExec", "CallToSystemExit" })
public class AntFileParser
{
  public static final Logger    logger                  = LogFactory.getLogger(AntFileParser.class);
  private File                  fileToParse;
  private List<Antfile>         antfiles;
  private List<Antfile>         importsAlreadyProcessed = new UniqueList<Antfile>();
  private List<Antfile>         importsToProcess        = new UniqueList<Antfile>();
  private Map<String, Property> properties              = new HashMap<String, Property>();
  private Os                    os;
  private AntScriptPreferences  preferences;
  private AntParserUiImpl       ui;
  private EventCollector        eventCollector          = new EventCollector();
  private static int            parseCount              = 0;

  /** Creates a new AntFileParser object. */
  public AntFileParser(Os os, AntScriptPreferences preferences, AntParserUiImpl ui, File... filesToParse)
  {
    this.os          = os;
    this.preferences = preferences;
    this.ui          = ui;
    fileToParse      = filesToParse[0];

    for (int i = 1; i < filesToParse.length; i++)
    {
      try
      {
        importsToProcess.add(new Antfile(filesToParse[i], properties));
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      catch (JDOMException e)
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * Executed for each Ant file chosen from the UI.
   *
   * @param  generateGraphicOutput
   */
  public List<Antfile> processBuildFile(boolean generateGraphicOutput)
  {
    eventCollector.clear();

    List<Antfile> antfiles = new ArrayList<Antfile>();

    try
    {
      antfiles = parse();
      writeOutputFiles(antfiles, generateGraphicOutput);
      processEvents();
    }
    catch (GenericException e)
    {
      processException(e);
    }
    catch (Exception e)
    {
      logger.error("There has been a severe error, stopping all activity.", e);
      e.printStackTrace();
      // todo something
      // System.exit(1);
    }

    return antfiles;
  }

  /**
   * first, parse for imports and antcalls (so you know all the possible build files) second, parse for all macrodefs and taskdefs (so they're
   * available from calls when targets are scanned) third, then scan targets for depends, usage of antcalls, taskdefs, and macrodefs.
   *
   * @return  the antfile that was specified from the file selector. This is needed, even though it's in the list of antfiles, because we need to know
   *          the file name for writing the .dot file later.
   */
  private List<Antfile> parse() throws GenericException
  {
    List<Taskdef>   taskdefs   = new UniqueList<Taskdef>();
    List<Macrodef>  macrodefs  = new UniqueList<Macrodef>();
    List<Path>      paths      = new UniqueList<Path>();
    List<Classpath> classpaths = new UniqueList<Classpath>();
    Antfile         antfile;

    try
    {
      antfile = new Antfile(fileToParse, properties);
      antfile.parse(this, importsToProcess, importsAlreadyProcessed, eventCollector, preferences);
      antfiles = new UniqueList<Antfile>();
      antfiles.add(antfile);
      parseAllFilesForAntUsage();
    }
    catch (IOException e)
    {
      logger.error("Problem opening the file", e);
      eventCollector.addEvent(new Event("Couldn't find or open imported Ant file: " + fileToParse.getName(), e));
      throw new GenericException(e);
    }
    catch (JDOMException e)
    {
      logger.error("Problem opening the file", e);
      eventCollector.addEvent(new Event("Couldn't parse Ant file: " + fileToParse.getName(), e));
      throw new GenericException(e);
    }

    if (preferences.shouldIncludeImportedFiles())
    {
      while (!importsToProcess.isEmpty() && (parseCount++ < 100))
      {
        System.out.println("AntFileParser.parse 2 antfile = " + antfile + " parse count = " + parseCount);
        parseForImports(antfile);
      }
    }

    for (Antfile theAntFile : antfiles)
    {
      logger.error("Processing bulk files " + theAntFile.getBuildFile());
      parseForMacrodefsAndTaskdefs(theAntFile, taskdefs, macrodefs);
      parseForPaths(theAntFile, paths);
      parseForClasspaths(theAntFile, classpaths);
    }

    for (Antfile theAntFile : antfiles)
    {
      logger.error("Processing imported files for macrodefs and taskdefs " + theAntFile.getBuildFile());
      parseAntfilesForMacrodefsAndTaskdefs(theAntFile, macrodefs, taskdefs);
    }

    for (Macrodef macrodef : macrodefs)
    {
      logger.error("Processing imported files for taskdef usage " + macrodef.getName());
      macrodef.parseForTaskdefUsage(taskdefs);
    }

    for (Antfile theAntFile : antfiles)
    {
      theAntFile.resolveInternalDependencies();
      theAntFile.resolveExternalDependencies(antfiles);
    }

    return antfiles;
  }

  /**
   * Go through all the files already known, and look for any usages of "ant", which could be using a file yet unknown. If so, add it to the list. In
   * any case, add the Ant call to the list of "depends" in the Target.
   */
  private void parseAllFilesForAntUsage() throws IOException, JDOMException
  {
    for (Antfile antfile : antfiles)
    {
      List<Antfile> moreAntFiles = antfile.parseForAnts(eventCollector);

      for (Antfile moreAntFile : moreAntFiles)
      {
        importsToProcess.add(moreAntFile);
      }
    }
  }

  /** Pretty self-explanitory name. */
  private void parseAntfilesForMacrodefsAndTaskdefs(Antfile anAntFile, List<Macrodef> macrodefs, List<Taskdef> taskdefs)
  {
    anAntFile.parseForMacrodefUsage(macrodefs);
    anAntFile.parseForTaskdefUsage(taskdefs);
  }

  /** Pretty self-explanitory name. */
  private void parseForClasspaths(Antfile theAntFile, List<Classpath> classpaths)
  {
    // todo
  }

  /** Pretty self-explanitory name. */
  private void parseForImports(Antfile antfile)
  {
    if (!importsToProcess.isEmpty())
    {
      Antfile fileToProcess = importsToProcess.get(0);

      if (fileToProcess != null)
      {
        importsToProcess.remove(fileToProcess);
        importsAlreadyProcessed.add(fileToProcess);

        File   buildFile = fileToProcess.getBuildFile();
        String fileName  = (buildFile != null) ? buildFile.getName()
                                               : "";

        try
        {
          Antfile anImportedAntfile = new Antfile(buildFile, properties);

          anImportedAntfile.parse(this, importsToProcess, importsAlreadyProcessed, eventCollector, preferences);
          antfile.addImport(anImportedAntfile);
          antfiles.add(anImportedAntfile);
        }
        catch (IOException e)
        {
          eventCollector.addEvent(new Event("Couldn't find or open imported Ant file: " + fileName, e));
        }
        catch (JDOMException e)
        {
          eventCollector.addEvent(new Event("Couldn't parse Ant file: " + fileName, e));
        }
      }
    }
  }

  /** Pretty self-explanitory name. */
  private void parseForMacrodefsAndTaskdefs(Antfile anAntFile, List<Taskdef> taskdefs, List<Macrodef> macrodefs)
  {
    // dgbtodo anAntFile.parseForAntcalls();???
    List<Taskdef>  antfileTaskdefs  = anAntFile.parseForTaskdefs();
    List<Macrodef> antfileMacrodefs = anAntFile.parseForMacrodefs();

    taskdefs.addAll(antfileTaskdefs);
    macrodefs.addAll(antfileMacrodefs);
  }

  /** Pretty self-explanitory name. */
  private void parseForPaths(Antfile theAntFile, List<Path> paths)
  {
    // todo
  }

  /** Handle any events that were generated during the processing. */
  private void processEvents()
  {
    List<Event> events = eventCollector.getEvents();
    String      output = "The following problems occured while parsing the ant files:\n";

    for (Event event : events)
    {
      output += ("    " + event.getReason() + "\n\t\t" + event.getException().getMessage() + "\n");
    }

    if (!events.isEmpty() && (ui != null))
    {
      JOptionPane.showMessageDialog(ui.getFrame(), output);
    }
  }

  /** This is pretty lame, but it'll get better over time... */
  private void processException(GenericException e)
  {
    StringBuilder       buffer     = new StringBuilder();
    StackTraceElement[] stackTrace = e.getStackTrace();

    for (StackTraceElement stackTraceElement : stackTrace)
    {
      buffer.append(stackTraceElement.toString()).append("\n");
    }

    String    extrainfo = "";
    Exception exception = e.getException();

    if ((exception instanceof IOException) || (exception instanceof JDOMException))
    {
      extrainfo = "there was an error parsing one of the xml files.  Do you have\n"
                    + "any properties in the imports or Ant calls?  That's not supported yet.";
    }

    if (ui != null)
    {
      JOptionPane.showMessageDialog(ui.getFrame(), "Something terrible happend: " + extrainfo + "\n\n" + buffer.toString());
    }
  }

  /**  */
  private void writeOutputFiles(List<Antfile> antfile, boolean generateGraphicOutput)
  {
    OutputHandler outputHandler = new OutputHandler(preferences, antfiles, this, os, generateGraphicOutput);

    outputHandler.writeOutputFiles(antfile);
  }

  /** Get the dependencies for all the build file nodes. */
  Map<Node, List<Dependency>> getDependencies()
  {
    Map<Node, List<Dependency>> dependencies = new HashMap<Node, List<Dependency>>();

    for (Antfile antfile : antfiles)
    {
      List<Target>   targets        = antfile.getTargets();
      List<Taskdef>  localTaskdefs  = antfile.getLocalTaskdefs();
      List<Macrodef> localMacrodefs = antfile.getLocalMacrodefs();

      for (Target target : targets)
      {
        List<Dependency> depends = target.getDepends();

        getDependenciesForNode(target, depends, dependencies);
      }

      for (Macrodef localMacrodef : localMacrodefs)
      {
        List<Dependency> depends = localMacrodef.getDepends();

        getDependenciesForNode(localMacrodef, depends, dependencies);
      }

      for (Taskdef localTaskdef : localTaskdefs)
      {
        List<Dependency> depends = new ArrayList<Dependency>();

        getDependenciesForNode(localTaskdef, depends, dependencies);
      }
    }

    return dependencies;
  }

  /** Get the dependencies for a particular node. */
  private void getDependenciesForNode(Node target, List<Dependency> depends, Map<Node, List<Dependency>> dependencies)
  {
    if (target.shouldPrint(preferences))
    {
      dependencies.put(target, depends);
    }
  }

  /** Pass-through for radio button value - should we include imported values? todo this method name sucks! */
  public boolean includeImportedFiles()
  {
    return preferences.shouldIncludeImportedFiles();
  }

  /**
   * Resolve any unresolved dependencies in the build files. This is because although all dependencies might be detected during initial parsing of a
   * target, they might not be fully resolved, and might need to be later.
   *
   * <p>For instance, if you have an imported buildfile, any calls to a target in that buildfile won't get resolved immediately. So, you need to know
   * which targets are fully resolved. After you've done that, any unresolved dependencies are probably lurking in other buildfiles - but it's a
   * simple matter to get those and set the proper buildfile in the depencency.</p>
   */
  private void resolveDependencies()
  {
    for (Antfile antfile : antfiles)
    {
      antfile.resolveInternalDependencies();
      antfile.resolveExternalDependencies(antfiles);
    }
  }

  public EventCollector getEventCollector()
  {
    return eventCollector;
  }
}
