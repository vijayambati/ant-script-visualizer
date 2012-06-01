/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 25, 2004 Time: 8:04:03 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import java.io.File;

/** This isn't really a unit test, just a test of making sure things execute the way I think they're going to. */
@SuppressWarnings({ "CallToRuntimeExec", "OverlyBroadCatchBlock" })
public class ExecTest
{
  public static final Logger  logger     = LogFactory.getLogger(ExecTest.class);
  private static final String dotPath    = "/Applications/Graphviz.app/Contents/MacOS/dot";
  private static File         outputFile;
  private static File         dotFile;
  private String              os         = System.getProperty("os.name");

  /** @param  args  dibble */
  public static void main(String[] args)
  {
    ExecTest execTest = new ExecTest();

    outputFile = new File("/Users/douglasbullard/Documents/JavaStuff/AntScriptVisualizer/testData/Import Test", "threebuild.pdf");
    dotFile    = new File("/Users/douglasbullard/Documents/JavaStuff/AntScriptVisualizer/testData/Import Test", "threebuild.dot");

    // execTest.testNothing();
    // logger.debug("");
    // execTest.testSingleQuotes();
    // logger.debug("");
    // execTest.testDoubleQuotes();
    // logger.debug("");
    // execTest.testDelimted();
    // logger.debug("");
    // execTest.testNonParsedExec();
    execTest.testPreparsedExec();
  }

  private boolean isOsX()
  {
    return os.toLowerCase().startsWith("mac os");
  }

  private boolean isWindows()
  {
    return (os.toLowerCase().startsWith("windows"));
  }

  private String concatenate(String[] command)
  {
    StringBuffer stringBuffer = new StringBuffer();

    for (String s : command)
    {
      stringBuffer.append(" ");
      stringBuffer.append(s);
    }

    return stringBuffer.toString();
  }

  /**  */
  private void testDelimted()
  {
    try
    {
      String dotFilePath    = dotFile.getAbsolutePath();
      String outputFilePath = outputFile.getAbsolutePath() + "_testDelimted.pdf";
      String command        = dotPath + " -Tpdf " + dotFilePath + " -o " + "\"" + StringUtils.replace(outputFilePath, " ", "\\ ") + "\"";

      if (logger.isDebugEnabled())
      {
        logger.debug("testDelimted Command to run: " + command);
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();

      if (isWindows())
      {
        command = "cmd.exe /c ";
      }
      else if (isOsX())
      {
        command = "/Applications/Preview.app/Contents/MacOS/Preview ";
      }

      command += ("\"" + StringUtils.replace(outputFilePath, " ", "\\ ") + "\"");

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + command);
      }

      runtime.exec(command);
    }
    catch (Exception e)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug(e);
      }
    }
  }

  /**  */
  private void testDoubleQuotes()
  {
    try
    {
      String dotFilePath    = dotFile.getAbsolutePath();
      String outputFilePath = outputFile.getAbsolutePath() + "_testDoubleQuotes.pdf";
      String command        = dotPath + " -Tpdf " + "\"" + dotFilePath + "\"" + " -o " + "\"" + outputFilePath + "\"";

      if (logger.isDebugEnabled())
      {
        logger.debug("testDoubleQuotes Command to run: " + command);
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();

      if (isWindows())
      {
        command = "cmd.exe /c ";
      }
      else if (isOsX())
      {
        command = "/Applications/Preview.app/Contents/MacOS/Preview ";
      }

      command += ("\"" + outputFilePath + "\"");

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + command);
      }

      runtime.exec(command);
    }
    catch (Exception e)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug(e);
      }
    }
  }

  /**  */
  private void testNothing()
  {
    try
    {
      String dotFilePath    = dotFile.getAbsolutePath();
      String outputFilePath = outputFile.getAbsolutePath() + "_testNothing.pdf";
      String command        = dotPath + " -Tpdf " + dotFilePath + " -o" + outputFilePath;

      if (logger.isDebugEnabled())
      {
        logger.debug("testNothing Command to run: " + command);
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();

      if (isWindows())
      {
        command = "cmd.exe /c ";
      }
      else if (isOsX())
      {
        command = "/Applications/Preview.app/Contents/MacOS/Preview ";
      }

      command += outputFilePath;

      if (logger.isDebugEnabled())
      {
        logger.debug("testNothing Command to run: " + command);
      }

      runtime.exec(command);
    }
    catch (Exception e)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug(e);
      }
    }
  }

  /**  */
  private void testPreparsedExec()
  {
    try
    {
      String   dotFilePath    = dotFile.getAbsolutePath();
      String   outputFilePath = outputFile.getAbsolutePath() + "_testPreparsedExec.pdf";
      String   cmd;
      String[] command        = { dotPath, "-Tpdf", dotFilePath, "-o", outputFilePath };

      if (logger.isDebugEnabled())
      {
        logger.debug("testPreparsedExec Command to run: " + concatenate(command));
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();

      if (isWindows())
      {
        cmd = "cmd.exe /c ";
      }
      else
      {
        cmd = "/Applications/Preview.app/Contents/MacOS/Preview";
      }

      command = new String[] { cmd, outputFilePath };

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + concatenate(command));
      }

      runtime.exec(command);
    }
    catch (Exception e)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug(e);
      }
    }
  }

  /**  */
  private void testSingleQuotes()
  {
    try
    {
      String dotFilePath    = dotFile.getAbsolutePath();
      String outputFilePath = outputFile.getAbsolutePath() + "_testSingleQuotes.pdf";
      String command        = dotPath + " -Tpdf " + "'" + dotFilePath + "'" + " -o " + "'" + outputFilePath + "'";

      if (logger.isDebugEnabled())
      {
        logger.debug("testSingleQuotes Command to run: " + command);
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();

      if (isWindows())
      {
        command = "cmd.exe /c ";
      }
      else if (isOsX())
      {
        command = "/Applications/Preview.app/Contents/MacOS/Preview ";
      }

      command += ("'" + outputFilePath + "'");

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + command);
      }

      runtime.exec(command);
    }
    catch (Exception e)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug(e);
      }
    }
  }
}
