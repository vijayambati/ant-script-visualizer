package com.nurflugel.util.antscriptvisualizer;

import static com.nurflugel.util.Os.OS_X;
import com.nurflugel.util.Os;
import com.nurflugel.util.antscriptvisualizer.nodes.Antfile;
import com.nurflugel.util.antscriptvisualizer.nodes.Dependency;
import com.nurflugel.util.antscriptvisualizer.nodes.Macrodef;
import com.nurflugel.util.antscriptvisualizer.nodes.Node;
import com.nurflugel.util.antscriptvisualizer.nodes.NodeWithDependancies;
import com.nurflugel.util.antscriptvisualizer.nodes.Target;
import com.nurflugel.util.antscriptvisualizer.nodes.Taskdef;
import static org.apache.commons.io.FileUtils.writeLines;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.replace;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is involved in parsing the Ant file and generating the DOT output.
 *
 * <p>Sounds like it's time to refactor, eh?</p>
 */
@SuppressWarnings({ "CallToRuntimeExec", "AssignmentToCollectionOrArrayFieldFromParameter" })
public class OutputHandler
{
  public static final Logger      logger                = LogFactory.getLogger(OutputHandler.class);
  protected static final String   CLOSING_LINE_DOTGRAPH = "}";
  protected static final String[] OPENING_LINE_DOTGRAPH =
  {
    "digraph G {",                                           //
    "node [shape=box,fontname=\"Arial\",fontsize=\"10\"];",  //
    "edge [fontname=\"Arial\",fontsize=\"8\"];",             //
    "rankdir=RL;",                                           //
    ""
  };
  protected static final String OPENING_LINE_SUBGRAPH = "subgraph ";
  private AntFileParser         parser;
  private Os                    os;
  private boolean               generateGraphicOutput;
  private Preferences           preferences;
  private List<Antfile>         antfiles;

  /**
   * @param  preferences            Preferences from the UI
   * @param  antfiles               List of Ant files to parse
   * @param  generateGraphicOutput  if true, the graphic output (PDF, PNG, etc) will be generated, if false, not.
   */
  public OutputHandler(Preferences preferences, List<Antfile> antfiles, AntFileParser antFileParser, Os os, boolean generateGraphicOutput)
  {
    this.preferences           = preferences;
    this.antfiles              = antfiles;
    parser                     = antFileParser;
    this.os                    = os;
    this.generateGraphicOutput = generateGraphicOutput;
  }
  // -------------------------- OTHER METHODS --------------------------

  /** Write all the output for the given ant files. */
  void writeOutputFiles(List<Antfile> antfile)
  {
    try
    {
      resolveDependencies();

      File dotFile = writeDotFile(antfile);

      if (generateGraphicOutput)
      {
        processDotFile(dotFile);
      }

      if (preferences.shouldDeleteDotFilesOnExit())
      {
        dotFile.deleteOnExit();
      }
    }
    catch (IOException e)
    {
      logger.error("Unexpected exception", e);
    }
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

  /** Write the resulting dot file to the file system. */
  private File writeDotFile(List<Antfile> file) throws IOException
  {
    String fileName = getDotFilename(file.get(0));
    File   dotFile  = new File(fileName);

    if (logger.isDebugEnabled())
    {
      logger.debug("Writing output to file " + dotFile.getAbsolutePath());
    }

    List<String> lines = new ArrayList<String>();

    lines.addAll(asList(OPENING_LINE_DOTGRAPH));

    if (!preferences.shouldGroupByBuildfiles())
    {
      lines.add("clusterrank=none;");
    }

    if (preferences.shouldConcentrate())
    {
      lines.add("concentrate=true;");
    }

    // todo only print primary build file if supposed to
    writeDotFileTargetDeclarations(lines);

    if (preferences.shouldShowLegend())
    {
      writeLegend(lines);
    }

    Map<Node, List<Dependency>> dependencies = parser.getDependencies();

    writeDotDependencies(dependencies, lines);
    lines.add(CLOSING_LINE_DOTGRAPH);
    lines = handleSpecialCharacters(lines);
    writeLines(dotFile, lines);

    return dotFile;
  }

  /** Get the dot file name from the ant file name. */
  private String getDotFilename(Antfile antFile)
  {
    File   file         = antFile.getBuildFile();
    String absolutePath = file.getAbsolutePath();
    String newPath      = replace(absolutePath, ".xml", ".dot");

    return newPath;
  }

  /** Write out all the dot declarations. */
  private void writeDotFileTargetDeclarations(List<String> lines) throws IOException
  {
    int clusterIndex = 0;

    for (Antfile antfile : antfiles)
    {
      lines.add("\t" + OPENING_LINE_SUBGRAPH + "cluster_" + clusterIndex + " {");

      String fileName;

      if (preferences.shouldUseAbsolutePaths())
      {
        fileName = antfile.getBuildFile().getAbsolutePath();
      }
      else
      {
        fileName = antfile.getBuildFile().getName();
      }

      lines.add("\t\tlabel=\"" + fileName + "\"");

      List<Target>   targets        = antfile.getTargets();
      List<Taskdef>  localTaskdefs  = antfile.getLocalTaskdefs();
      List<Macrodef> localMacrodefs = antfile.getLocalMacrodefs();

      for (Target target : targets)
      {
        writeOutputForNode(target, lines);
      }

      for (Macrodef localMacrodef : localMacrodefs)
      {
        writeOutputForNode(localMacrodef, lines);
      }

      for (Taskdef localTaskdef : localTaskdefs)
      {
        writeOutputForNode(localTaskdef, lines);
      }

      lines.add("\t}");
      clusterIndex++;
    }  // end for
  }

  /**  */
  private void writeOutputForNode(Node node, List<String> lines) throws IOException
  {
    node.writeOutput(lines, preferences);
  }

  /** Generates a legend. */
  private void writeLegend(List<String> lines) throws IOException
  {
    lines.add("\t" + OPENING_LINE_SUBGRAPH + "cluster_legend {");
    lines.add("\t\tlabel=\"legend\"");
    lines.add("\t\ttarget [label=\"target\" shape=box color=black ];");
    lines.add("\t\ttarget2 [label=\"target\" shape=box color=black ];");
    lines.add("\t\ttarget3 [label=\"target\" shape=box color=black ];");
    lines.add("\t\ttaskdef [label=\"taskdef\" shape=hexagon color=green ];");
    lines.add("\t\tmacrodef [label=\"macrodef\" shape=ellipse color=red ]; ");
    lines.add("\t}");
    lines.add("\ttarget -> taskdef;");
    lines.add("\ttarget -> macrodef;");
    lines.add("\ttarget -> target2[label=<ant> color=red,style=dotted];");
    lines.add("\ttarget -> target3[label=<antcall> color=green,style=dotted];");
  }

  /** Write out the dependencies. */
  @SuppressWarnings({ "OverlyNestedMethod" })
  private void writeDotDependencies(Map<Node, List<Dependency>> dependencies, List<String> lines) throws IOException
  {
    Set<Node>   set       = dependencies.keySet();
    Set<String> resultSet = new HashSet<String>();

    for (Object aSet : set)
    {
      Node theNode = (Node) aSet;

      if (theNode instanceof NodeWithDependancies)
      {
        if (theNode.shouldPrint(preferences))
        {
          NodeWithDependancies node           = (NodeWithDependancies) theNode;
          String               niceName       = node.getNiceName();
          List<Dependency>     dependentNodes = node.getDepends();

          for (Dependency dependantNode : dependentNodes)
          {
            if (dependantNode.getName() != null)
            {
              String dependantNodeNiceName = dependantNode.getNiceName();
              String dependencyExtraInfo   = dependantNode.getDependencyExtraInfo();

              if (dependantNode.shouldPrint(preferences))
              {
                String line = "\t\t" + niceName + " -> " + dependantNodeNiceName + dependencyExtraInfo + ";";

                resultSet.add(line);
              }
            }
          }
        }
      }
    }  // end for

    for (Object aResultSet : resultSet)
    {
      String line = (String) aResultSet;

      lines.add(line);
    }
  }

  /** Replace characters that DOT doesn't like, such as : (file names on Windows may have c:\ in them... */
  private List<String> handleSpecialCharacters(List<String> lines)
  {
    List<String> newLines = new ArrayList<String>(lines.size());

    for (String line : lines)
    {
      line = StringUtils.replace(line, ":", "_");
      newLines.add(line);
    }

    return newLines;
  }

  /** Convert the .dot file into png, pdf, svg, whatever. */
  @SuppressWarnings({ "OverlyLongMethod" })
  private void processDotFile(File dotFile)
  {
    try
    {
      String outputFileName = getOutputFileName(dotFile, preferences.getOutputFormat().getExtension());
      File   outputFile     = new File(dotFile.getParent(), outputFileName);
      File   parentFile     = outputFile.getParentFile();
      String dotFilePath    = dotFile.getAbsolutePath();
      String outputFilePath = outputFile.getAbsolutePath();

      if (outputFile.exists())
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("Deleting existing version of " + outputFilePath);
        }

        outputFile.delete();  // delete the file before generating it if it exists
      }

      String outputFormatName  = preferences.getOutputFormat().getType();
      String dotExecutablePath = preferences.getDotExecutablePath();

      // this is to deal with different versions of Graphviz on OS X - if dot is in applications (old version), preface with an e for epdf.  If it's
      // in /usr/local/bin, leave as pdf
      if ((os == OS_X) && dotExecutablePath.startsWith("/Applications") && !outputFormatName.startsWith("e"))
      {
        outputFormatName = "e" + outputFormatName;
      }

      String[] command = { dotExecutablePath, "-T" + outputFormatName, "-o" + outputFilePath, dotFilePath };

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + concatenate(command) + " parent file is " + parentFile.getPath());
      }

      Runtime runtime = Runtime.getRuntime();
      long    start   = new Date().getTime();

      runtime.exec(command).waitFor();

      long end = new Date().getTime();

      if (logger.isDebugEnabled())
      {
        logger.debug("Took " + (end - start) + " milliseconds to generate graphic");
      }

      os.openFile(outputFilePath);
    }
    catch (Exception e)  // todo handle error
    {
      logger.error(e);
    }
  }

  /** Takes someting like build.dot and returns build.png. */
  private String getOutputFileName(File dotFile, String outputExtension)
  {
    String results = dotFile.getName();
    int    index   = results.indexOf(".dot");

    results = results.substring(0, index) + outputExtension;

    return results;
  }

  /** Join the array together as one string, with spaces between the elements. */
  private String concatenate(String[] commands)
  {
    StringBuilder stringBuffer = new StringBuilder();

    for (String command : commands)
    {
      stringBuffer.append(" ");
      stringBuffer.append(command);
    }

    return stringBuffer.toString().trim();
  }
}
