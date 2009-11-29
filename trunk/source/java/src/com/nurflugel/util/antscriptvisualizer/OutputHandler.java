package com.nurflugel.util.antscriptvisualizer;

import com.apple.eio.FileManager;
import com.nurflugel.util.antscriptvisualizer.nodes.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import static com.nurflugel.util.antscriptvisualizer.Os.*;

/**
 * This class is involved in parsing the Ant file and generating the DOT output.
 *
 * <p>Sounds like it's time to refactor, eh?</p>
 */
@SuppressWarnings({ "CallToRuntimeExec", "AssignmentToCollectionOrArrayFieldFromParameter" })
public class OutputHandler
{
  public static final Logger    logger                = LogFactory.getLogger(OutputHandler.class);
  public static final String    NEW_LINE              = "\n";
  protected static final String CLOSING_LINE_DOTGRAPH = "}";
  protected static final String OPENING_LINE_DOTGRAPH = "digraph G {\nnode [shape=box,fontname=\"Arial\",fontsize=\"10\"];\nedge [fontname=\"Arial\",fontsize=\"8\"];\nrankdir=RL;\n\n";
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
      logger.debug(e);
    }
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
        logger.debug("Deleting existing version of " + outputFilePath);
        outputFile.delete();  // delete the file before generating it if it exists
      }

      String   outputFormat = preferences.getOutputFormat().getType();
      String[] command      = { preferences.getDotExecutablePath(), "-T" + outputFormat, dotFilePath, "-o" + outputFilePath };

      logger.debug("Command to run: " + concatenate(command) + " parent file is " + parentFile.getPath());

      Runtime runtime = Runtime.getRuntime();
      long    start   = new Date().getTime();

      runtime.exec(command).waitFor();

      long end = new Date().getTime();

      logger.debug("Took " + (end - start) + " milliseconds to generate graphic");

      List<String> commandList = new ArrayList<String>();

      if (os == OS_X)
      {
        // This method doesn't work
        // calling FileManager to open the URL works, if we replace spaces with %20
        outputFilePath = outputFilePath.replace(" ", "%20");

        String fileUrl = "file://" + outputFilePath;

        logger.debug("Trying to open URL: " + fileUrl);

        try
        {
          Class<?> aClass = Class.forName("com.apple.eio.FileManager");
          Method   method = aClass.getMethod("openURL", String.class);

          method.invoke(null, fileUrl);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      else
      {
        if (os == WINDOWS)
        {
          commandList.add("cmd.exe");
          commandList.add("/c");
        }

        commandList.add(outputFilePath);
        command = commandList.toArray(new String[commandList.size()]);
        logger.debug("Command to run: " + concatenate(command));

        runtime.exec(command);
      }
    }
    catch (Exception e)  // todo handle error
    {
      logger.error(e);
    }
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

  /** Takes someting like build.dot and returns build.png. */
  private String getOutputFileName(File dotFile, String outputExtension)
  {
    String results = dotFile.getName();
    int    index   = results.indexOf(".dot");

    results = results.substring(0, index) + outputExtension;

    return results;
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

    logger.debug("Writing output to file " + dotFile.getAbsolutePath());

    OutputStream     outputStream = new FileOutputStream(dotFile);
    DataOutputStream out          = new DataOutputStream(outputStream);

    // open a new .dot file
    out.writeBytes(OPENING_LINE_DOTGRAPH);

    if (!preferences.shouldGroupByBuildfiles())
    {
      out.writeBytes("clusterrank=none;\n");
    }

    if (preferences.shouldConcentrate())
    {
      out.writeBytes("concentrate=true;\n");
    }

    // todo only print primary build file if supposed to
    writeDotFileTargetDeclarations(out);

    if (preferences.shouldShowLegend())
    {
      writeLegend(out);
    }

    Map<Node, List<Node>> dependencies = parser.getDependencies();

    writeDotDependencies(dependencies, out);

    out.writeBytes(CLOSING_LINE_DOTGRAPH);

    outputStream.close();

    return dotFile;
  }

  /** Generates a legend. */
  private void writeLegend(DataOutputStream out) throws IOException
  {
    out.writeBytes("\t" + OPENING_LINE_SUBGRAPH + "cluster_legend {" + NEW_LINE);

    out.writeBytes("\t\tlabel=\"legend\"" + NEW_LINE);

    out.writeBytes("\t\ttarget [label=\"target\" shape=box color=black ];" + NEW_LINE);
    out.writeBytes("\t\ttarget2 [label=\"target\" shape=box color=black ];" + NEW_LINE);
    out.writeBytes("\t\ttarget3 [label=\"target\" shape=box color=black ];" + NEW_LINE);
    out.writeBytes("\t\ttaskdef [label=\"taskdef\" shape=hexagon color=green ];" + NEW_LINE);
    out.writeBytes("\t\tmacrodef [label=\"macrodef\" shape=ellipse color=red ]; " + NEW_LINE);
    out.writeBytes("\t}" + NEW_LINE);
    out.writeBytes("\ttarget -> taskdef;" + NEW_LINE);
    out.writeBytes("\ttarget -> macrodef;" + NEW_LINE);
    out.writeBytes("\ttarget -> target2[label=<ant> color=red,style=dotted];" + NEW_LINE);
    out.writeBytes("\ttarget -> target3[label=<antcall> color=green,style=dotted];" + NEW_LINE);
  }

  /** Get the dot file name from the ant file name. */
  private String getDotFilename(Antfile antFile)
  {
    File   file         = antFile.getBuildFile();
    String absolutePath = file.getAbsolutePath();
    String newPath      = StringUtils.replace(absolutePath, ".xml", ".dot");

    return newPath;
  }

  /** Write out the dependencies. */
  @SuppressWarnings({ "OverlyNestedMethod" })
  private void writeDotDependencies(Map<Node, List<Node>> dependencies, DataOutputStream out) throws IOException
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
          List<Node>           dependentNodes = node.getDepends();

          for (Node dependantNode : dependentNodes)
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

      out.writeBytes(line + NEW_LINE);
    }
  }

  /** Write out all the dot declarations. */
  private void writeDotFileTargetDeclarations(DataOutputStream out) throws IOException
  {
    int clusterIndex = 0;

    for (Antfile antfile : antfiles)
    {
      out.writeBytes("\t" + OPENING_LINE_SUBGRAPH + "cluster_" + clusterIndex + " {" + NEW_LINE);

      String fileName;

      if (preferences.shouldUseAbsolutePaths())
      {
        fileName = antfile.getBuildFile().getAbsolutePath();
      }
      else
      {
        fileName = antfile.getBuildFile().getName();
      }

      out.writeBytes("\t\tlabel=\"" + fileName + "\"" + NEW_LINE);

      List<Target>   targets        = antfile.getTargets();
      List<Taskdef>  localTaskdefs  = antfile.getLocalTaskdefs();
      List<Macrodef> localMacrodefs = antfile.getLocalMacrodefs();

      for (Target target : targets)
      {
        writeOutputForNode(target, out);
      }

      for (Macrodef localMacrodef : localMacrodefs)
      {
        writeOutputForNode(localMacrodef, out);
      }

      for (Taskdef localTaskdef : localTaskdefs)
      {
        writeOutputForNode(localTaskdef, out);
      }

      out.writeBytes("\t}" + NEW_LINE);

      clusterIndex++;
    }  // end for
  }

  /**  */
  private void writeOutputForNode(Node node, DataOutputStream out) throws IOException
  {
    node.writeOutput(out, preferences);
  }
}
